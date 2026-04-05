package com.artemchep.literaryclock.data.firestore

import com.artemchep.literaryclock.data.room.QuoteEntity

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

    fun toEntity(momentKey: Int) = QuoteEntity(
        key = key,
        quote = quote,
        title = title,
        author = author,
        asin = asin,
        momentKey = momentKey,
    )

}
