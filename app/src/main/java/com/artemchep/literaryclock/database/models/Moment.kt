package com.artemchep.literaryclock.database.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * @author Artem Chepurnoy
 */
open class Moment : RealmObject() {

    @PrimaryKey
    var key: Int = 0

    lateinit var quotes: RealmList<Quote>

}
