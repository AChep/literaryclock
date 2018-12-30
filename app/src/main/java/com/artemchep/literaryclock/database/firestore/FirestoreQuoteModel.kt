package com.artemchep.literaryclock.database.firestore

import com.artemchep.literaryclock.database.models.Quote

/**
 * @author Artem Chepurnoy
 */
data class FirestoreQuoteModel(
    var key: String = "",
    var quote: String = "",
    var title: String = "",
    var author: String = "",
    var asin: String = "",
    var time: Int = 0
) {

    fun toRealmModel() = Quote()
        .also { out ->
            out.key = key
            out.quote = quote
            out.title = title
            out.author = author
            out.asin = asin
        }

}
