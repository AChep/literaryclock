package com.artemchep.literaryclock.store.factory

import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.data.realm.RealmQuoteModel

/**
 * @author Artem Chepurnoy
 */
object QuoteItemFactory {

    fun transform(origin: RealmQuoteModel): QuoteItem {
        return QuoteItem(quote = origin.quote, title = origin.title, asin = origin.asin, author = origin.author)
    }

}
