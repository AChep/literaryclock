package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import com.artemchep.literaryclock.checkout.FlexCheckout
import com.artemchep.literaryclock.logic.live.ProductLiveData
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.solovyev.android.checkout.Purchase
import org.solovyev.android.checkout.RequestListener

@RunWith(RobolectricTestRunner::class)
class DonateViewModelTest {
    private val application = ApplicationProvider.getApplicationContext<Application>()

    @Test
    fun successfulPurchaseReloadsInventoryWhenProductsAreObserved() {
        val viewModel = DonateViewModel(application)
        val checkout = mock<FlexCheckout>()
        val productLiveData = mock<ProductLiveData>()
        whenever(productLiveData.hasActiveObservers()).thenReturn(true)
        viewModel.replaceCheckoutLiveData(MutableLiveData(checkout))
        viewModel.replaceProductLiveData(productLiveData)

        viewModel.purchaseListener().onSuccess(mock())

        verify(productLiveData).loadInventory(checkout)
    }

    private fun DonateViewModel.replaceCheckoutLiveData(checkoutLiveData: MutableLiveData<FlexCheckout>) {
        val field = DonateViewModel::class.java.getDeclaredField("checkoutLiveData")
        field.isAccessible = true
        field.set(this, checkoutLiveData)
    }

    private fun DonateViewModel.replaceProductLiveData(productLiveData: ProductLiveData) {
        val field = DonateViewModel::class.java.getDeclaredField("productLiveData")
        field.isAccessible = true
        field.set(this, productLiveData)
    }

    @Suppress("UNCHECKED_CAST")
    private fun DonateViewModel.purchaseListener(): RequestListener<Purchase> {
        val field = DonateViewModel::class.java.getDeclaredField("requestListener")
        field.isAccessible = true
        return field.get(this) as RequestListener<Purchase>
    }
}
