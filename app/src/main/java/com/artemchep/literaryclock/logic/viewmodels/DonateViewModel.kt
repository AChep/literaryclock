package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.artemchep.literaryclock.logic.live.CheckoutLiveData
import com.artemchep.literaryclock.logic.live.ProductLiveData
import org.solovyev.android.checkout.Sku

/**
 * @author Artem Chepurnoy
 */
class DonateViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG = "DonateViewModel"
    }

    val checkoutLiveData = CheckoutLiveData(application)

    val productLiveData = ProductLiveData(checkoutLiveData)

    fun purchase(fragment: Fragment, sku: Sku) {
        if (!checkoutLiveData.hasActiveObservers()) {
            val msg = "You should be subscribed to a checkout live data to perform this operation."
            Log.w(TAG, msg)
            return
        }

        val checkout = checkoutLiveData.value!!
        checkout.withFragment(fragment) {

        }
    }

}
