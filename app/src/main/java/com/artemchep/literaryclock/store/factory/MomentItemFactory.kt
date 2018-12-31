package com.artemchep.literaryclock.store.factory

import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.data.realm.RealmMomentModel

/**
 * @author Artem Chepurnoy
 */
object MomentItemFactory {

    fun transform(origin: RealmMomentModel): MomentItem {
        return if (origin.isValid) {
            MomentItem(origin.quotes.map(QuoteItemFactory::transform))
        } else MomentItem(listOf())
    }

}