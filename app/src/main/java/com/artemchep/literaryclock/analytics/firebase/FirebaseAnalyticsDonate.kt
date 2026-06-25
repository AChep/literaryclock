package com.artemchep.literaryclock.analytics.firebase

import android.os.Bundle
import com.artemchep.literaryclock.analytics.AnalyticsDonate
import com.google.firebase.analytics.FirebaseAnalytics
import org.solovyev.android.checkout.Sku

/**
 * @author Artem Chepurnoy
 */
class FirebaseAnalyticsDonate(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsDonate {

    override fun logDonateSkuOpen(sku: Sku) {
        val bundle = Bundle().apply {
            putDouble(FirebaseAnalytics.Param.VALUE, sku.detailedPrice.amount / PRICE_MICROS_PER_UNIT)
            putString(FirebaseAnalytics.Param.CURRENCY, sku.detailedPrice.currency)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle)
    }

    private companion object {
        const val PRICE_MICROS_PER_UNIT = 1_000_000.0
    }

}
