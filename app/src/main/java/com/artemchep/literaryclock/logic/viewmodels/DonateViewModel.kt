package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.artemchep.literaryclock.analytics.AnalyticsDonate
import com.artemchep.literaryclock.checkout.FlexCheckout
import com.artemchep.literaryclock.logic.live.CheckoutLiveData
import com.artemchep.literaryclock.logic.live.ProductLiveData
import org.kodein.di.instance
import org.solovyev.android.checkout.*

/**
 * @author Artem Chepurnoy
 */
class DonateViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        const val TAG = "DonateViewModel"
    }

    private val analytics by instance<AnalyticsDonate>()

    private val requestListener = object : RequestListener<Purchase> {
        override fun onSuccess(result: Purchase) {
            onComplete(wasOwned = false)
        }

        override fun onError(response: Int, e: Exception) {
            when (response) {
                ResponseCodes.ITEM_ALREADY_OWNED -> onComplete(wasOwned = true)
            }
        }

        private fun onComplete(wasOwned: Boolean) {
            if (wasOwned) {
                // Nothing has changed, so we don't need
                // to reload the inventory.
                return
            }

            // Update the inventory.
            if (!productLiveData.hasActiveObservers()) {
                productLiveData.loadInventory()
            }
        }
    }

    val checkoutLiveData: LiveData<FlexCheckout> = MediatorLiveData<FlexCheckout>()
        .apply {
            val source = CheckoutLiveData(application)
            addSource(source) { checkout ->
                // Add the singleton purchase flow
                // listener.
                checkout.createPurchaseFlow(requestListener)

                // Mirror value.
                postValue(checkout)
            }
        }

    val productLiveData = ProductLiveData(checkoutLiveData)

    /**
     * Handle fragment/activity on result
     * calls.
     */
    fun result(requestCode: Int, resultCode: Int, data: Intent?) {
        val checkout = checkoutLiveData.value
        checkout?.onActivityResult(requestCode, resultCode, data)
    }

    fun purchase(intentStarter: IntentStarter, sku: Sku) {
        if (!checkoutLiveData.hasActiveObservers()) {
            val msg = "You should be subscribed to a checkout live data to perform this operation."
            Log.w(TAG, msg)
            return
        }

        val checkout = checkoutLiveData.value!!
        checkout.intentStarter = intentStarter
        checkout.whenReady(object : Checkout.EmptyListener() {
            override fun onReady(requests: BillingRequests) {
                requests.purchase(sku, null, checkout.purchaseFlow)
            }
        })

        analytics.logDonateSkuOpen(sku)
    }

}
