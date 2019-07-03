package com.artemchep.literaryclock.analytics

import com.artemchep.literaryclock.models.QuoteItem

/**
 * @author Artem Chepurnoy
 */
interface AnalyticsMain : Analytics {

    fun logQuoteOpen(quote: QuoteItem)

    fun logQuoteShare(quote: QuoteItem)

}