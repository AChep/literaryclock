package com.artemchep.literaryclock.data.firestore

import com.artemchep.literaryclock.data.realm.RealmQuoteModel

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

    fun toRealmModel() = RealmQuoteModel()
        .also { out ->
            out.key = key
            out.quote = quote
            out.title = title
            out.author = author
            out.asin = asin
        }

}
