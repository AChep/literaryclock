package com.artemchep.literaryclock.database.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * @author Artem Chepurnoy
 */
open class Quote : RealmObject() {

    @PrimaryKey
    var key : Int = 0

    var quote: String = ""

    var title: String = ""

    var author: String = ""

    var asin: String = ""

}