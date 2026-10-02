package com.artemchep.literaryclock.billing

import android.app.Activity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LifecycleOwner
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsResult
import com.artemchep.literaryclock.models.Loader
import com.artemchep.literaryclock.models.Message
import com.artemchep.literaryclock.test.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class DonationBillingRepositoryTest {
    private val dispatcher = UnconfinedTestDispatcher()
    @get:Rule val mainDispatcherRule = MainDispatcherRule(dispatcher)
    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val owner = mock<LifecycleOwner>()
    private val clients = mutableListOf<Connection>()
    private val messages = mutableListOf<Message>()
    private val repository = DonationBillingRepository(
        clientFactory = { listener -> Connection(listener).also(clients::add).client },
        verifier = PurchaseVerifier { it.signature == "valid" },
        scope = CoroutineScope(SupervisorJob() + dispatcher),
        sendMessage = messages::add,
    )
    private val connection get() = clients.last()

    @After
    fun close() {
        repository.onStop(owner)
    }

    @Test
    fun loadsLocalizedProductsInPriceOrderAndSkipsUnavailableOffers() {
        start()
        val unavailable = product("donation_5", 5_000_000L)
        whenever(unavailable.oneTimePurchaseOfferDetails).thenReturn(null)
        inventory(listOf(product("donation_2", 2_000_000L), unavailable, product()))

        assertThat(products().map { it.id }).containsExactly("donation_1", "donation_2").inOrder()
        assertThat(products().first().formattedPrice).isEqualTo("€1.00")
        assertThat(products().first().currencyCode).isEqualTo("EUR")
        assertThat(products().all { it.canPurchase }).isTrue()
    }

    @Test
    fun noAvailableProductsShowsError() {
        start()
        inventory(emptyList())
        assertThat(repository.products.value).isInstanceOf(Loader.Error::class.java)
    }

    @Test
    fun setupFailureCanBeRetried() {
        repository.onStart(owner)
        connection.setup.onBillingSetupFinished(result(BillingResponseCode.BILLING_UNAVAILABLE))
        assertThat(repository.products.value).isInstanceOf(Loader.Error::class.java)
        repository.refresh()
        connection.ready()
        inventory()
        assertThat(products()).hasSize(1)
    }

    @Test
    fun olderInventoryResponsesCannotOverwriteNewerResults() {
        start()
        repository.refresh()
        inventory(listOf(product("donation_2")))
        connection.productQueries.first().onProductDetailsResponse(result(), queried(listOf(product())))
        connection.purchaseQueries.first().onQueryPurchasesResponse(result(), listOf(purchase()))
        assertThat(products().single().id).isEqualTo("donation_2")
        assertThat(connection.acknowledgements).isEmpty()
    }

    @Test
    fun restoresOwnershipAndAcknowledgesOnlyUnacknowledgedKnownPurchases() {
        start()
        inventory(
            listOf(product(), product("donation_2")),
            listOf(purchase(), purchase("donation_2", acknowledged = true), purchase("unrelated")),
        )
        assertThat(products().all { it.state == DonationState.PURCHASED && !it.canPurchase }).isTrue()
        assertThat(connection.acknowledgements.map { it.first }).containsExactly("token-donation_1")
    }

    @Test
    fun successfulPurchaseInvalidatesOlderSnapshotsAndDeduplicatesAcknowledgement() {
        start()
        inventory()
        repository.refresh()
        val staleQuery = connection.purchaseQueries.last()
        connection.updates.onPurchasesUpdated(result(), listOf(purchase()))
        staleQuery.onQueryPurchasesResponse(result(), emptyList())
        inventory(purchases = listOf(purchase()))
        connection.updates.onPurchasesUpdated(result(), listOf(purchase()))
        inventory(purchases = listOf(purchase()))
        assertThat(products().single().state).isEqualTo(DonationState.PURCHASED)
        assertThat(connection.acknowledgements).hasSize(1)
        connection.acknowledgements.single().second.onAcknowledgePurchaseResponse(result())
        repository.refresh()
        inventory(purchases = listOf(purchase()))
        assertThat(connection.acknowledgements).hasSize(1)
    }

    @Test
    fun pendingDonationCannotBeRepurchasedOrAcknowledgedUntilCompleted() {
        start()
        inventory(purchases = listOf(purchase(state = Purchase.PurchaseState.PENDING)))
        assertThat(products().single().state).isEqualTo(DonationState.PENDING)
        assertThat(products().single().canPurchase).isFalse()
        assertThat(repository.purchase(mock(), "donation_1")).isFalse()
        assertThat(connection.acknowledgements).isEmpty()

        connection.updates.onPurchasesUpdated(result(), listOf(purchase()))
        inventory(purchases = listOf(purchase()))
        assertThat(products().single().state).isEqualTo(DonationState.PURCHASED)
        assertThat(connection.acknowledgements).hasSize(1)
    }

    @Test
    fun cancelledPendingPurchaseIsAvailableAfterSuccessfulRefresh() {
        start()
        inventory(purchases = listOf(purchase(state = Purchase.PurchaseState.PENDING)))
        repository.refresh()
        inventory()
        assertThat(products().single().state).isEqualTo(DonationState.AVAILABLE)
    }

    @Test
    fun failedOwnershipQueryDoesNotDiscardKnownOwnership() {
        start()
        inventory(purchases = listOf(purchase(acknowledged = true)))
        repository.refresh()
        connection.productQueries.last().onProductDetailsResponse(result(), queried(listOf(product())))
        connection.purchaseQueries.last().onQueryPurchasesResponse(result(BillingResponseCode.NETWORK_ERROR), emptyList())
        assertThat(repository.products.value).isInstanceOf(Loader.Error::class.java)
        assertThat(repository.purchase(mock(), "donation_1")).isFalse()
        assertThat(messages).isEmpty()
    }

    @Test
    fun failedProductQueryShowsError() {
        start()
        connection.productQueries.last().onProductDetailsResponse(result(BillingResponseCode.NETWORK_ERROR), queried(emptyList()))
        connection.purchaseQueries.last().onQueryPurchasesResponse(result(), emptyList())
        assertThat(repository.products.value).isInstanceOf(Loader.Error::class.java)
    }

    @Test
    fun invalidSignatureNeverGrantsOwnershipOrAcknowledges() {
        start()
        inventory()
        connection.updates.onPurchasesUpdated(result(), listOf(purchase(valid = false)))
        inventory(purchases = listOf(purchase(valid = false)))
        assertThat(repository.products.value).isInstanceOf(Loader.Error::class.java)
        assertThat(connection.acknowledgements).isEmpty()
        assertThat(messages).hasSize(1)
    }

    @Test
    fun launchPreventsDoubleClicksAndCancellationIsQuiet() {
        start()
        inventory()
        val activity = mock<Activity>()
        assertThat(repository.purchase(activity, "donation_1")).isTrue()
        assertThat(products().single().canPurchase).isFalse()
        assertThat(repository.purchase(activity, "donation_1")).isFalse()
        verify(connection.client, times(1)).launchBillingFlow(any(), any())
        connection.updates.onPurchasesUpdated(result(BillingResponseCode.USER_CANCELED), null)
        assertThat(products().single().canPurchase).isTrue()
        assertThat(messages).isEmpty()
    }

    @Test
    fun recoveredPurchaseReleasesLaunchLockWhenUpdateCallbackWasMissed() {
        start()
        val catalog = listOf(product(), product("donation_2"))
        inventory(catalog)
        assertThat(repository.purchase(mock(), "donation_1")).isTrue()

        repository.refresh()
        inventory(catalog, listOf(purchase()))

        val recovered = products().first { it.id == "donation_1" }
        assertThat(recovered.state).isEqualTo(DonationState.PURCHASED)
        assertThat(recovered.canPurchase).isFalse()
        assertThat(products().first { it.id == "donation_2" }.canPurchase).isTrue()
        assertThat(connection.acknowledgements.map { it.first }).containsExactly("token-donation_1")
        assertThat(repository.purchase(mock(), "donation_2")).isTrue()
        assertThat(messages).isEmpty()
    }

    @Test
    fun recoveredPendingPurchaseAllowsAnotherDonationWithoutAcknowledging() {
        start()
        val catalog = listOf(product(), product("donation_2"))
        inventory(catalog)
        assertThat(repository.purchase(mock(), "donation_1")).isTrue()

        repository.refresh()
        inventory(catalog, listOf(purchase(state = Purchase.PurchaseState.PENDING)))

        val recovered = products().first { it.id == "donation_1" }
        assertThat(recovered.state).isEqualTo(DonationState.PENDING)
        assertThat(recovered.canPurchase).isFalse()
        assertThat(products().first { it.id == "donation_2" }.canPurchase).isTrue()
        assertThat(connection.acknowledgements).isEmpty()
        assertThat(repository.purchase(mock(), "donation_2")).isTrue()
    }

    @Test
    fun inventoryWithoutLaunchedProductKeepsLaunchLocked() {
        start()
        val catalog = listOf(product(), product("donation_2"), product("donation_5"))
        inventory(catalog)
        assertThat(repository.purchase(mock(), "donation_1")).isTrue()

        repository.refresh()
        inventory(catalog)
        assertThat(products().any { it.canPurchase }).isFalse()
        assertThat(repository.purchase(mock(), "donation_5")).isFalse()

        repository.refresh()
        inventory(catalog, listOf(purchase("donation_2", acknowledged = true)))
        assertThat(products().any { it.canPurchase }).isFalse()
        assertThat(repository.purchase(mock(), "donation_5")).isFalse()
        verify(connection.client, times(1)).launchBillingFlow(any(), any())
    }

    @Test
    fun alreadyOwnedLaunchRefreshesInsteadOfReportingFailure() {
        start()
        inventory()
        whenever(connection.client.launchBillingFlow(any(), any())).thenReturn(result(BillingResponseCode.ITEM_ALREADY_OWNED))
        assertThat(repository.purchase(mock(), "donation_1")).isFalse()
        inventory(purchases = listOf(purchase(acknowledged = true)))
        assertThat(products().single().state).isEqualTo(DonationState.PURCHASED)
        assertThat(messages).isEmpty()
    }

    @Test
    fun launchFailureReleasesPurchaseLockAndShowsMessage() {
        start()
        inventory()
        whenever(connection.client.launchBillingFlow(any(), any())).thenReturn(result(BillingResponseCode.BILLING_UNAVAILABLE))
        assertThat(repository.purchase(mock(), "donation_1")).isFalse()
        assertThat(products().single().canPurchase).isTrue()
        connection.updates.onPurchasesUpdated(result(BillingResponseCode.BILLING_UNAVAILABLE), null)
        assertThat(messages).hasSize(1)
    }

    @Test
    fun restoredAcknowledgementCancelsScheduledRetry() {
        start()
        inventory(purchases = listOf(purchase()))
        connection.acknowledgements.single().second.onAcknowledgePurchaseResponse(result(BillingResponseCode.NETWORK_ERROR))
        repository.refresh()
        inventory(purchases = listOf(purchase(acknowledged = true)))
        dispatcher.scheduler.advanceUntilIdle()
        assertThat(connection.acknowledgements).hasSize(1)
        assertThat(messages).isEmpty()
    }

    @Test
    fun permanentAcknowledgementFailureDoesNotRetryInBackground() {
        start()
        inventory(purchases = listOf(purchase()))
        connection.acknowledgements.single().second.onAcknowledgePurchaseResponse(result(BillingResponseCode.DEVELOPER_ERROR))
        dispatcher.scheduler.advanceUntilIdle()
        assertThat(connection.acknowledgements).hasSize(1)
        assertThat(messages).hasSize(1)
    }

    @Test
    fun nullableLegacyOfferTokenCanStillLaunch() {
        start()
        val details = product()
        whenever(details.oneTimePurchaseOfferDetails!!.offerToken).thenReturn(null)
        inventory(listOf(details))
        assertThat(repository.purchase(mock(), "donation_1")).isTrue()
    }

    @Test
    fun foregroundRecreatesClientAndIgnoresOldCallbacks() {
        start()
        inventory()
        val previous = connection
        repository.onStop(owner)
        verify(previous.client).endConnection()
        start()
        inventory(listOf(product("donation_2")))
        previous.updates.onPurchasesUpdated(result(), listOf(purchase()))
        assertThat(products().single().id).isEqualTo("donation_2")
        assertThat(connection.acknowledgements).isEmpty()
    }

    @Test
    fun duplicateForegroundStartDoesNotCreateAnotherClient() {
        start()
        repository.onStart(owner)
        assertThat(clients).hasSize(1)
    }

    @Test
    fun transientAcknowledgementFailuresRetryWithBoundedBackoff() {
        start()
        inventory(purchases = listOf(purchase()))
        repeat(4) { attempt ->
            assertThat(connection.acknowledgements).hasSize(attempt + 1)
            connection.acknowledgements.last().second.onAcknowledgePurchaseResponse(result(BillingResponseCode.NETWORK_ERROR))
            dispatcher.scheduler.advanceUntilIdle()
        }
        assertThat(connection.acknowledgements).hasSize(4)
        assertThat(messages).hasSize(1)

        repository.onStop(owner)
        start()
        inventory(purchases = listOf(purchase()))
        assertThat(connection.acknowledgements).hasSize(1)
    }

    @Test
    fun backgroundingCancelsScheduledAcknowledgementRetries() {
        start()
        inventory(purchases = listOf(purchase()))
        val previous = connection
        previous.acknowledgements.single().second.onAcknowledgePurchaseResponse(result(BillingResponseCode.NETWORK_ERROR))
        repository.onStop(owner)
        dispatcher.scheduler.advanceUntilIdle()
        assertThat(previous.acknowledgements).hasSize(1)
    }

    private fun start() {
        repository.onStart(owner)
        connection.ready()
    }

    private fun inventory(
        products: List<ProductDetails> = listOf(product()),
        purchases: List<Purchase> = emptyList(),
    ) {
        connection.productQueries.last().onProductDetailsResponse(result(), queried(products))
        connection.purchaseQueries.last().onQueryPurchasesResponse(result(), purchases)
    }

    private fun products(): List<DonationProduct> = (repository.products.value as Loader.Ok).value

    private inner class Connection(val updates: PurchasesUpdatedListener) {
        val client = mock<BillingClient>()
        lateinit var setup: BillingClientStateListener
        val productQueries = mutableListOf<ProductDetailsResponseListener>()
        val purchaseQueries = mutableListOf<PurchasesResponseListener>()
        val acknowledgements = mutableListOf<Pair<String, AcknowledgePurchaseResponseListener>>()

        init {
            whenever(client.startConnection(any())).thenAnswer {
                setup = it.getArgument(0)
                null
            }
            whenever(client.queryProductDetailsAsync(any(), any())).thenAnswer {
                productQueries += it.getArgument<ProductDetailsResponseListener>(1)
                null
            }
            whenever(client.queryPurchasesAsync(any(), any())).thenAnswer {
                purchaseQueries += it.getArgument<PurchasesResponseListener>(1)
                null
            }
            whenever(client.acknowledgePurchase(any(), any())).thenAnswer {
                acknowledgements += it.getArgument<AcknowledgePurchaseParams>(0).purchaseToken to it.getArgument<AcknowledgePurchaseResponseListener>(1)
                null
            }
            whenever(client.launchBillingFlow(any(), any())).thenReturn(result())
        }

        fun ready() {
            whenever(client.isReady).thenReturn(true)
            setup.onBillingSetupFinished(result())
        }
    }

    private fun queried(products: List<ProductDetails>) = QueryProductDetailsResult.create(products, emptyList())

    private fun result(code: Int = BillingResponseCode.OK): BillingResult =
        BillingResult.newBuilder().setResponseCode(code).build()

    private fun product(id: String = "donation_1", price: Long = 1_000_000L): ProductDetails {
        val offer = mock<ProductDetails.OneTimePurchaseOfferDetails>()
        whenever(offer.priceAmountMicros).thenReturn(price)
        whenever(offer.formattedPrice).thenReturn("€1.00")
        whenever(offer.priceCurrencyCode).thenReturn("EUR")
        whenever(offer.offerToken).thenReturn("offer-$id")
        // BillingFlowParams also reads internal String metadata from ProductDetails.
        val product = mock<ProductDetails>(defaultAnswer = Answers.RETURNS_SMART_NULLS)
        whenever(product.productId).thenReturn(id)
        whenever(product.productType).thenReturn(BillingClient.ProductType.INAPP)
        whenever(product.name).thenReturn("Donation")
        whenever(product.description).thenReturn("Support development")
        whenever(product.oneTimePurchaseOfferDetails).thenReturn(offer)
        return product
    }

    private fun purchase(
        id: String = "donation_1",
        state: Int = Purchase.PurchaseState.PURCHASED,
        acknowledged: Boolean = false,
        valid: Boolean = true,
    ): Purchase {
        val purchase = mock<Purchase>()
        whenever(purchase.products).thenReturn(listOf(id))
        whenever(purchase.purchaseState).thenReturn(state)
        whenever(purchase.purchaseToken).thenReturn("token-$id")
        whenever(purchase.isAcknowledged).thenReturn(acknowledged)
        whenever(purchase.signature).thenReturn(if (valid) "valid" else "invalid")
        return purchase
    }
}
