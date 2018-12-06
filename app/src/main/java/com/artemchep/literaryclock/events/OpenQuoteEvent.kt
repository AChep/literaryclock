package com.artemchep.literaryclock.events

import com.artemchep.literaryclock.models.QuoteItem

/**
 * @author Artem Chepurnoy
 */
data class OpenQuoteEvent(
    val info: ClickEventInfo,
    val quote: QuoteItem
)
