package com.artemchep.literaryclock.logic.live

import android.content.Context
import androidx.lifecycle.LiveData
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.checkout.FlexCheckout

/**
 * Live data that starts a [FlexCheckout] if someone observes it and
 * stops it later.
 *
 * @author Artem Chepurnoy
 */
class CheckoutLiveData(private val context: Context) : LiveData<FlexCheckout>() {

    companion object {
        const val TAG = "CheckoutLiveData"
    }

    private val checkout by lazy {
        val application = context.applicationContext as Heart
        return@lazy FlexCheckout(TAG, application.billing)
    }

    override fun onActive() {
        super.onActive()
        // Start the checkout process.
        checkout.start()

        // Stopping the checkout clears its purchase flows, so consumers
        // must be notified again to configure it after each restart.
        value = checkout
    }

    override fun onInactive() {
        checkout.stop()
        super.onInactive()
    }

}
