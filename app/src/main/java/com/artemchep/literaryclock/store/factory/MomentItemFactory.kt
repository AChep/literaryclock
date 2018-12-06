package com.artemchep.literaryclock.store.factory

import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.database.models.Moment

/**
 * @author Artem Chepurnoy
 */
object MomentItemFactory {

    fun transform(origin: Moment): MomentItem {
        return if (origin.isValid) {
            MomentItem(origin.quotes.map(QuoteItemFactory::transform))
        } else MomentItem(listOf())
    }

}