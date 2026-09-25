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
        private const val MINUTES_PER_DAY = 24 * 60

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
        val requestedTimes = range.start.time..range.endInclusive.time
        val start = range.start.time.floorMod(MINUTES_PER_DAY)
        val end = range.endInclusive.time.floorMod(MINUTES_PER_DAY)
        val moments = (if (range.endInclusive.time.toLong() - range.start.time >= MINUTES_PER_DAY) {
            dao.getMoments(0, MINUTES_PER_DAY - 1)
        } else if (start <= end) {
            dao.getMoments(start, end)
        } else {
            dao.getMoments(start, MINUTES_PER_DAY - 1) + dao.getMoments(0, end)
        })
            .associateBy { it.moment.key }
        return requestedTimes
            .map { time ->
                val key = time.floorMod(MINUTES_PER_DAY)
                moments[key]?.let(MomentItemFactory::transform) ?: emptyMoment
            }
    }

}

private fun Int.floorMod(other: Int): Int = ((this % other) + other) % other
