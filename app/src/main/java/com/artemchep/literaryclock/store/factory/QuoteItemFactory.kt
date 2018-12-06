package com.artemchep.literaryclock.store.factory

import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.database.models.Quote

/**
 * @author Artem Chepurnoy
 */
object QuoteItemFactory {

    fun transform(origin: Quote): QuoteItem {
        return QuoteItem(quote = origin.quote, title = origin.title, asin = origin.asin, author = origin.author)
    }

}
