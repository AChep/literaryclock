package com.artemchep.literaryclock.data.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * @author Artem Chepurnoy
 */
open class RealmQuoteModel : RealmObject() {

    @PrimaryKey
    var key: String = ""

    var quote: String = ""

    var title: String = ""

    var author: String = ""

    var asin: String = ""

}
