package com.artemchep.literaryclock.analytics.firebase

import androidx.core.os.bundleOf
import com.artemchep.literaryclock.analytics.AnalyticsMain
import com.artemchep.literaryclock.models.QuoteItem
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * @author Artem Chepurnoy
 */
class FirebaseAnalyticsMain(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsMain {

    override fun logQuoteOpen(quote: QuoteItem) {
        val bundle = bundleOf(
            FirebaseAnalytics.Param.ITEM_CATEGORY to FirebaseAnalyticsContract.VIEW_ITEM_CATEGORY_QUOTE,
            FirebaseAnalytics.Param.ITEM_ID to quote.asin,
            FirebaseAnalytics.Param.ITEM_NAME to quote.title
        )
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    override fun logQuoteShare(quote: QuoteItem) {
        val bundle = bundleOf(
            FirebaseAnalytics.Param.CONTENT_TYPE to FirebaseAnalyticsContract.SHARE_CATEGORY_QUOTE,
            FirebaseAnalytics.Param.ITEM_ID to quote.asin,
            FirebaseAnalytics.Param.METHOD to FirebaseAnalyticsContract.SHARE_METHOD_SYSTEM
        )
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
    }

}