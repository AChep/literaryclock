package com.artemchep.literaryclock.billing

import android.app.Activity
import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.artemchep.literaryclock.BuildConfig
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.listOfSkus
import com.artemchep.literaryclock.messageLiveEvent
import com.artemchep.literaryclock.models.Loader
import com.artemchep.literaryclock.models.Message
import com.artemchep.literaryclock.models.MessageType
import com.artemchep.literaryclock.models.message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** One client per foreground session. All mutable state is confined to the main thread. */
@MainThread
class DonationBillingRepository internal constructor(
    private val clientFactory: (PurchasesUpdatedListener) -> BillingClient,
    private val verifier: PurchaseVerifier,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
    private val sendMessage: (Message) -> Unit = { messageLiveEvent.value = it },
) : DefaultLifecycleObserver {
    constructor(context: Context) : this(
        clientFactory = playClientFactory(context.applicationContext),
        verifier = PlayPurchaseVerifier(BuildConfig.LICENSE_KEY),
    )

    private val productIds = listOfSkus().toSet()
    private val productDetailsParams = QueryProductDetailsParams.newBuilder().setProductList(
        productIds.map { id ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id).setProductType(BillingClient.ProductType.INAPP).build()
        },
    ).build()
    private val purchasesParams = QueryPurchasesParams.newBuilder()
        .setProductType(BillingClient.ProductType.INAPP).build()
    private val mutableProducts = MutableLiveData<Loader<List<DonationProduct>>>(Loader.Loading())
    val products: LiveData<Loader<List<DonationProduct>>> = mutableProducts

    private var client: BillingClient? = null
    private var connecting = false
    private var loadGeneration = 0
    private var ownershipLoaded = false
    private var inFlightProductId: String? = null
    private var details = emptyMap<String, ProductDetails>()
    private val ownership = mutableMapOf<String, DonationState>()
    private val acknowledging = mutableSetOf<String>()
    private val acknowledged = mutableSetOf<String>()
    private val acknowledgementRetries = mutableMapOf<String, Job>()

    override fun onStart(owner: LifecycleOwner) {
        if (client != null) return
        lateinit var connection: BillingClient
        connection = clientFactory { result, purchases ->
            dispatch(connection) { onPurchasesUpdated(connection, result, purchases) }
        }
        client = connection
        refresh()
    }

    override fun onStop(owner: LifecycleOwner) {
        val previous = client
        client = null // Ignore callbacks from a connection that is being torn down.
        loadGeneration++
        connecting = false
        inFlightProductId = null
        ownershipLoaded = false
        details = emptyMap()
        acknowledgementRetries.values.forEach(Job::cancel)
        acknowledgementRetries.clear()
        acknowledging.clear()
        previous?.endConnection()
        mutableProducts.value = Loader.Loading()
    }

    fun refresh() {
        val connection = client ?: return
        if (connection.isReady) {
            loadInventory(connection)
        } else if (!connecting) {
            connecting = true
            mutableProducts.value = Loader.Loading()
            connection.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) = dispatch(connection) {
                    connecting = false
                    if (result.responseCode == BillingResponseCode.OK) {
                        loadInventory(connection)
                    } else {
                        mutableProducts.value = Loader.Error()
                    }
                }

                override fun onBillingServiceDisconnected() = dispatch(connection) {
                    connecting = false
                    // Subsequent API calls reconnect automatically; a screen refresh can
                    // also retry a connection that never finished its initial setup.
                }
            })
        }
    }

    private fun loadInventory(connection: BillingClient) {
        val generation = ++loadGeneration
        var productsLoaded = false
        var purchasesLoaded = false
        var failed = false
        mutableProducts.value = Loader.Loading()

        fun complete() {
            if (productsLoaded && purchasesLoaded) {
                if (failed) mutableProducts.value = Loader.Error() else publishProducts()
            }
        }

        connection.queryProductDetailsAsync(productDetailsParams) { result, queried ->
            dispatch(connection) {
                if (generation != loadGeneration) return@dispatch
                productsLoaded = true
                if (result.responseCode == BillingResponseCode.OK) {
                    details = queried.productDetailsList
                        .filter { it.productId in productIds && it.oneTimePurchaseOfferDetails != null }
                        .associateBy { it.productId }
                } else {
                    failed = true
                }
                complete()
            }
        }
        connection.queryPurchasesAsync(purchasesParams) { result, purchases ->
            dispatch(connection) {
                if (generation != loadGeneration) return@dispatch
                purchasesLoaded = true
                if (result.responseCode == BillingResponseCode.OK &&
                    processPurchases(connection, purchases, replace = true)
                ) {
                    ownershipLoaded = true
                    // Recover the launch even when its purchase update callback was missed.
                    val productId = inFlightProductId
                    if (productId != null && productId in ownership) {
                        inFlightProductId = null
                    }
                } else {
                    // Failed queries must not make a known purchase available again.
                    failed = true
                }
                complete()
            }
        }
    }

    /** Returns true only when Play accepted the launch. The Activity is never stored. */
    fun purchase(activity: Activity, productId: String): Boolean {
        if (inFlightProductId != null || ownership[productId] != null) return false
        val connection = client
        val product = details[productId]
        val offer = product?.oneTimePurchaseOfferDetails
        if (connection == null || !connection.isReady || !ownershipLoaded || offer == null ||
            mutableProducts.value !is Loader.Ok || activity.isFinishing || activity.isDestroyed
        ) {
            showError(R.string.donation_purchase_failed)
            refresh()
            return false
        }

        val offerToken = offer.offerToken
        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(product)
            .apply { if (!offerToken.isNullOrEmpty()) setOfferToken(offerToken) }
            .build()
        inFlightProductId = productId
        publishProducts()
        val result = connection.launchBillingFlow(
            activity,
            BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(productParams)).build(),
        )
        if (result.responseCode != BillingResponseCode.OK) {
            // Play may also deliver the launch error to PurchasesUpdatedListener.
            if (inFlightProductId != null) {
                inFlightProductId = null
                handlePurchaseError(result)
            }
            return false
        }
        return true
    }

    private fun onPurchasesUpdated(
        connection: BillingClient,
        result: BillingResult,
        purchases: List<Purchase>?,
    ) {
        val wasInFlight = inFlightProductId != null
        inFlightProductId = null
        if (result.responseCode == BillingResponseCode.OK) {
            // Invalidate outstanding inventory snapshots before applying newer events.
            loadGeneration++
            if (purchases == null || !processPurchases(connection, purchases, replace = false)) {
                showError(R.string.donation_purchase_failed)
            }
            publishProducts()
            refresh()
        } else if (wasInFlight) {
            handlePurchaseError(result)
        } else if (result.responseCode == BillingResponseCode.ITEM_ALREADY_OWNED) {
            refresh()
        }
    }

    private fun handlePurchaseError(result: BillingResult) {
        publishProducts()
        when (result.responseCode) {
            BillingResponseCode.USER_CANCELED -> Unit
            BillingResponseCode.ITEM_ALREADY_OWNED -> refresh()
            else -> showError(R.string.donation_purchase_failed)
        }
    }

    private fun processPurchases(
        connection: BillingClient,
        purchases: List<Purchase>,
        replace: Boolean,
    ): Boolean {
        val states = mutableMapOf<String, DonationState>()
        val completed = mutableListOf<Purchase>()
        for (purchase in purchases) {
            val ids = purchase.products.filter { it in productIds }
            if (ids.isEmpty()) continue
            val state = when (purchase.purchaseState) {
                Purchase.PurchaseState.PENDING -> DonationState.PENDING
                Purchase.PurchaseState.PURCHASED -> {
                    if (!verifier.verify(purchase)) return false
                    completed += purchase
                    DonationState.PURCHASED
                }
                else -> continue
            }
            // A purchase outranks a pending one for the same product.
            ids.forEach { id -> states.mergeState(id, state) }
        }
        if (replace) ownership.clear()
        states.forEach { (id, state) -> ownership.mergeState(id, state) }
        completed.forEach { purchase ->
            val token = purchase.purchaseToken
            if (purchase.isAcknowledged) {
                acknowledged += token
                finishAcknowledgement(token)
            } else if (token !in acknowledged && acknowledging.add(token)) {
                acknowledge(connection, token, attempt = 0)
            }
        }
        return true
    }

    private fun acknowledge(connection: BillingClient, token: String, attempt: Int) {
        connection.acknowledgePurchase(
            AcknowledgePurchaseParams.newBuilder().setPurchaseToken(token).build(),
        ) { result ->
            dispatch(connection) {
                if (token in acknowledged) return@dispatch
                if (result.responseCode == BillingResponseCode.OK) {
                    acknowledged += token
                    finishAcknowledgement(token)
                } else if (result.responseCode in retryableResponses && attempt < 3) {
                    acknowledgementRetries[token] = scope.launch {
                        delay(1_000L shl attempt)
                        if (client === connection) acknowledge(connection, token, attempt + 1)
                    }
                } else {
                    finishAcknowledgement(token)
                    showError(R.string.donation_confirmation_failed)
                }
            }
        }
    }

    private fun finishAcknowledgement(token: String) {
        acknowledging -= token
        acknowledgementRetries.remove(token)?.cancel()
    }

    private fun publishProducts() {
        if (details.isEmpty()) {
            mutableProducts.value = Loader.Error()
            return
        }
        mutableProducts.value = Loader.Ok(details.values.map { product ->
            val offer = requireNotNull(product.oneTimePurchaseOfferDetails)
            val state = ownership[product.productId] ?: DonationState.AVAILABLE
            DonationProduct(
                id = product.productId,
                title = product.name,
                description = product.description,
                formattedPrice = offer.formattedPrice,
                priceAmountMicros = offer.priceAmountMicros,
                currencyCode = offer.priceCurrencyCode,
                state = state,
                canPurchase = ownershipLoaded && inFlightProductId == null && state == DonationState.AVAILABLE,
            )
        }.sortedBy { it.priceAmountMicros })
    }

    private fun dispatch(connection: BillingClient, block: () -> Unit) {
        scope.launch { if (client === connection) block() }
    }

    private fun showError(resource: Int) {
        sendMessage(message {
            type = MessageType.ERROR
            setTextStringRes(resource)
        })
    }

    private companion object {
        fun playClientFactory(appContext: Context) = { listener: PurchasesUpdatedListener ->
            BillingClient.newBuilder(appContext)
                .setListener(listener)
                .enablePendingPurchases(
                    PendingPurchasesParams.newBuilder().enableOneTimeProducts().build(),
                )
                .enableAutoServiceReconnection()
                .build()
        }

        /** [DonationState] is ordered by precedence, so the strongest known state wins. */
        fun MutableMap<String, DonationState>.mergeState(id: String, state: DonationState) {
            this[id] = maxOf(state, this[id] ?: state)
        }

        val retryableResponses = setOf(
            BillingResponseCode.SERVICE_DISCONNECTED,
            BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingResponseCode.NETWORK_ERROR,
            BillingResponseCode.ERROR,
        )
    }
}
