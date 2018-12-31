package com.artemchep.literaryclock.data.realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * @author Artem Chepurnoy
 */
open class RealmMomentModel : RealmObject() {

    @PrimaryKey
    var key: Int = 0

    lateinit var quotes: RealmList<RealmQuoteModel>

}
