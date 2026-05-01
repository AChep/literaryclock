package com.artemchep.literaryclock.test

import com.artemchep.literaryclock.analytics.AnalyticsMain
import com.artemchep.literaryclock.models.QuoteItem

class RecordingAnalyticsMain : AnalyticsMain {
    var favoritesOpenedCount = 0
    val openedQuotes = mutableListOf<QuoteItem>()
    val sharedQuotes = mutableListOf<QuoteItem>()
    val favoriteAddedQuotes = mutableListOf<QuoteItem>()
    val favoriteRemovedQuotes = mutableListOf<QuoteItem>()

    override fun logFavoritesOpen() {
        favoritesOpenedCount += 1
    }

    override fun logQuoteOpen(quote: QuoteItem) {
        openedQuotes += quote
    }

    override fun logQuoteShare(quote: QuoteItem) {
        sharedQuotes += quote
    }

    override fun logQuoteFavoriteAdd(quote: QuoteItem) {
        favoriteAddedQuotes += quote
    }

    override fun logQuoteFavoriteRemove(quote: QuoteItem) {
        favoriteRemovedQuotes += quote
    }
}
