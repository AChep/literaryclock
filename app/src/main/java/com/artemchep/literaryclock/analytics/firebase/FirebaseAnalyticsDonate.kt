package com.artemchep.literaryclock.analytics.firebase

import androidx.core.os.bundleOf
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
        val bundle = bundleOf(
            FirebaseAnalytics.Param.VALUE to sku.detailedPrice.amount.toDouble(),
            FirebaseAnalytics.Param.CURRENCY to sku.detailedPrice.currency
        )
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle)
    }

}