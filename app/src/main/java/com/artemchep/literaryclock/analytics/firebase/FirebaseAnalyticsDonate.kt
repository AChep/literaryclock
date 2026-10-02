package com.artemchep.literaryclock.analytics.firebase

import android.os.Bundle
import com.artemchep.literaryclock.analytics.AnalyticsDonate
import com.google.firebase.analytics.FirebaseAnalytics
import com.artemchep.literaryclock.billing.DonationProduct

/**
 * @author Artem Chepurnoy
 */
class FirebaseAnalyticsDonate(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsDonate {

    override fun logDonateSkuOpen(product: DonationProduct) {
        val bundle = Bundle().apply {
            putDouble(FirebaseAnalytics.Param.VALUE, product.priceAmountMicros / PRICE_MICROS_PER_UNIT)
            putString(FirebaseAnalytics.Param.CURRENCY, product.currencyCode)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle)
    }

    private companion object {
        const val PRICE_MICROS_PER_UNIT = 1_000_000.0
    }

}
