package com.artemchep.literaryclock.logic.viewmodels

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import com.artemchep.literaryclock.analytics.AnalyticsDonate
import com.artemchep.literaryclock.billing.DonationBillingRepository
import com.artemchep.literaryclock.billing.DonationProduct
import com.artemchep.literaryclock.models.Loader
import com.artemchep.literaryclock.test.testDonationProduct
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DonateViewModelTest {
    private val application = ApplicationProvider.getApplicationContext<Application>()
    private val billing = mock<DonationBillingRepository>()
    private val analytics = mock<AnalyticsDonate>()
    private val product = testDonationProduct()

    @Test
    fun exposesRepositoryProductsAndRefreshesOnRequest() {
        val products = MutableLiveData<Loader<List<DonationProduct>>>()
        whenever(billing.products).thenReturn(products)
        val viewModel = DonateViewModel(application, billing, analytics)
        assertThat(viewModel.productLiveData).isSameInstanceAs(products)
        viewModel.refresh()
        verify(billing).refresh()
    }

    @Test
    fun acceptedPurchaseLaunchLogsCheckout() {
        val activity = mock<Activity>()
        whenever(billing.purchase(activity, product.id)).thenReturn(true)
        DonateViewModel(application, billing, analytics).purchase(activity, product)
        verify(analytics).logDonateSkuOpen(product)
    }

    @Test
    fun rejectedOrDuplicateLaunchDoesNotLogCheckout() {
        DonateViewModel(application, billing, analytics).purchase(mock(), product)
        verifyNoInteractions(analytics)
    }
}
