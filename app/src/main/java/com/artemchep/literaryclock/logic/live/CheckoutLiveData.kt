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

        // Post the checkout instance when we went active
        // a first time.
        value ?: setValue(checkout)
    }

    override fun onInactive() {
        checkout.stop()
        super.onInactive()
    }

}
