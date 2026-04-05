package com.artemchep.literaryclock.data

import com.artemchep.literaryclock.data.room.LiteraryClockDao
import com.artemchep.literaryclock.data.room.MomentEntity
import com.artemchep.literaryclock.data.room.MomentWithQuotes
import com.artemchep.literaryclock.data.room.QuoteEntity
import com.artemchep.literaryclock.logic.live.MomentLiveData
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.widget.LiteraryWidgetUpdater
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RepoImplTest {
    @Test
    fun returnsRequestedMomentsForUiRange() = runBlocking {
        val start = 10
        val end = start + MomentLiveData.RANGE_SIZE
        val repo = RepoImpl(
            dao = FakeLiteraryClockDao(
                moments = (start..end).map(::momentWithSingleQuote),
            ),
        )

        val moments = repo.getMoments(Time(start)..Time(end))

        assertEquals(MomentLiveData.RANGE_SIZE + 1, moments.size)
        assertEquals("quote-10", moments.first().quotes.single().quote)
        assertEquals("quote-15", moments.last().quotes.single().quote)
    }

    @Test
    fun returnsRequestedMomentsForWidgetRange() = runBlocking {
        val start = 100
        val end = start + LiteraryWidgetUpdater.RANGE_SIZE
        val repo = RepoImpl(
            dao = FakeLiteraryClockDao(
                moments = (start..end).map(::momentWithSingleQuote),
            ),
        )

        val moments = repo.getMoments(Time(start)..Time(end))

        assertEquals(LiteraryWidgetUpdater.RANGE_SIZE + 1, moments.size)
        assertEquals("quote-100", moments.first().quotes.single().quote)
        assertEquals("quote-160", moments.last().quotes.single().quote)
    }

    @Test
    fun fillsMissingMinutesWithFallbackQuote() = runBlocking {
        val repo = RepoImpl(
            dao = FakeLiteraryClockDao(
                moments = listOf(
                    momentWithSingleQuote(20),
                    momentWithSingleQuote(22),
                ),
            ),
        )

        val moments = repo.getMoments(Time(20)..Time(22))

        assertEquals(3, moments.size)
        assertEquals("quote-20", moments[0].quotes.single().quote)
        assertTrue(moments[1].quotes.single().quote.contains("There's no quote for this time yet"))
        assertEquals("quote-22", moments[2].quotes.single().quote)
    }

    private class FakeLiteraryClockDao(
        private val moments: List<MomentWithQuotes>,
    ) : LiteraryClockDao {
        override suspend fun getMoments(start: Int, end: Int): List<MomentWithQuotes> =
            moments
                .filter { it.moment.key in start..end }
                .sortedBy { it.moment.key }

        override suspend fun countMoments(): Int = moments.size

        override suspend fun insertMoments(moments: List<MomentEntity>) {
            throw NotImplementedError()
        }

        override suspend fun insertQuotes(quotes: List<QuoteEntity>) {
            throw NotImplementedError()
        }

        override suspend fun deleteAllQuotes() {
            throw NotImplementedError()
        }

        override suspend fun deleteAllMoments() {
            throw NotImplementedError()
        }
    }

    companion object {
        private fun momentWithSingleQuote(time: Int) = MomentWithQuotes(
            moment = MomentEntity(key = time),
            quotes = listOf(
                QuoteEntity(
                    key = "key-$time",
                    quote = "quote-$time",
                    title = "title-$time",
                    author = "author-$time",
                    asin = "asin-$time",
                    momentKey = time,
                ),
            ),
        )
    }
}
