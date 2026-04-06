package com.artemchep.literaryclock.analytics

import com.artemchep.literaryclock.models.QuoteItem

/**
 * @author Artem Chepurnoy
 */
interface AnalyticsMain : Analytics {

    fun logFavoritesOpen()

    fun logQuoteOpen(quote: QuoteItem)

    fun logQuoteShare(quote: QuoteItem)

    fun logQuoteFavoriteAdd(quote: QuoteItem)

    fun logQuoteFavoriteRemove(quote: QuoteItem)

}
