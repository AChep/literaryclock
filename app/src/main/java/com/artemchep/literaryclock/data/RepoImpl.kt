package com.artemchep.literaryclock.data

import com.artemchep.literaryclock.data.room.LiteraryClockDao
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.store.factory.MomentItemFactory
import com.artemchep.literaryclock.store.factory.QuoteItemFactory

/**
 * @author Artem Chepurnoy
 */
class RepoImpl(
    private val dao: LiteraryClockDao,
) : Repo {

    companion object {
        private val emptyMoment = MomentItem(
            quotes = listOf(
                QuoteItemFactory.transform(
                    key = "",
                    quote = "There's no quote for this time yet. " +
                            "Try connecting to internet for database to sync.",
                    title = "",
                    asin = "",
                    author = "",
                    isPlaceholder = true,
                ),
            ),
        )
    }

    override suspend fun getMoments(range: ClosedRange<Time>): List<MomentItem> {
        val moments = dao
            .getMoments(range.start.time, range.endInclusive.time)
            .associateBy { it.moment.key }
        return (range.start.time..range.endInclusive.time)
            .map { time ->
                moments[time]?.let(MomentItemFactory::transform) ?: emptyMoment
            }
    }

}
