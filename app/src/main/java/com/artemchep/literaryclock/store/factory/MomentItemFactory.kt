package com.artemchep.literaryclock.store.factory

import com.artemchep.literaryclock.data.room.MomentWithQuotes
import com.artemchep.literaryclock.models.MomentItem

/**
 * @author Artem Chepurnoy
 */
object MomentItemFactory {

    fun transform(origin: MomentWithQuotes): MomentItem =
        MomentItem(origin.quotes.map(QuoteItemFactory::transform))

}
