package com.artemchep.literaryclock.analytics.firebase

import android.os.Bundle
import com.artemchep.literaryclock.analytics.AnalyticsMain
import com.artemchep.literaryclock.models.QuoteItem
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * @author Artem Chepurnoy
 */
class FirebaseAnalyticsMain(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsMain {

    override fun logFavoritesOpen() {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, FAVORITES_ITEM_CATEGORY)
            putString(FirebaseAnalytics.Param.ITEM_ID, FAVORITES_ITEM_ID)
            putString(FirebaseAnalytics.Param.ITEM_NAME, FAVORITES_ITEM_NAME)
        }
        firebaseAnalytics.logEvent(FAVORITES_OPEN_EVENT, bundle)
    }

    override fun logQuoteOpen(quote: QuoteItem) {
        val bundle = Bundle().apply {
            putString(
                FirebaseAnalytics.Param.ITEM_CATEGORY,
                FirebaseAnalyticsContract.VIEW_ITEM_CATEGORY_QUOTE
            )
            putString(FirebaseAnalytics.Param.ITEM_ID, quote.asin)
            putString(FirebaseAnalytics.Param.ITEM_NAME, quote.title)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    override fun logQuoteShare(quote: QuoteItem) {
        val bundle = Bundle().apply {
            putString(
                FirebaseAnalytics.Param.CONTENT_TYPE,
                FirebaseAnalyticsContract.SHARE_CATEGORY_QUOTE
            )
            putString(FirebaseAnalytics.Param.ITEM_ID, quote.asin)
            putString(FirebaseAnalytics.Param.METHOD, FirebaseAnalyticsContract.SHARE_METHOD_SYSTEM)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
    }

    override fun logQuoteFavoriteAdd(quote: QuoteItem) {
        logFavoriteEvent(FAVORITE_ADD_EVENT, quote)
    }

    override fun logQuoteFavoriteRemove(quote: QuoteItem) {
        logFavoriteEvent(FAVORITE_REMOVE_EVENT, quote)
    }

    private fun logFavoriteEvent(eventName: String, quote: QuoteItem) {
        val bundle = Bundle().apply {
            putString(
                FirebaseAnalytics.Param.ITEM_CATEGORY,
                FirebaseAnalyticsContract.VIEW_ITEM_CATEGORY_QUOTE
            )
            putString(FirebaseAnalytics.Param.ITEM_ID, quote.asin)
            putString(FirebaseAnalytics.Param.ITEM_NAME, quote.title)
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    private companion object {
        const val FAVORITES_ITEM_CATEGORY = "favorites"
        const val FAVORITES_ITEM_ID = "favorites_screen"
        const val FAVORITES_ITEM_NAME = "Favorites"

        const val FAVORITES_OPEN_EVENT = "favorites_open"
        const val FAVORITE_ADD_EVENT = "favorite_add"
        const val FAVORITE_REMOVE_EVENT = "favorite_remove"
    }

}
