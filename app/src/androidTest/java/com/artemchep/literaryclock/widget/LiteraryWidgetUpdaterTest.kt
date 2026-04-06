package com.artemchep.literaryclock.widget

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.data.room.LiteraryClockDatabase
import com.artemchep.literaryclock.data.room.MomentEntity
import com.artemchep.literaryclock.data.room.QuoteEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.kodein.di.direct
import org.kodein.di.instance

@RunWith(AndroidJUnit4::class)
class LiteraryWidgetUpdaterTest {
    private lateinit var context: Context
    private lateinit var database: LiteraryClockDatabase

    @Before
    fun setUp() = runBlocking {
        context = ApplicationProvider.getApplicationContext()
        val app = context.applicationContext as Heart
        database = app.di.direct.instance()
        database.clearAllTables()
    }

    @After
    fun tearDown() = runBlocking {
        database.clearAllTables()
    }

    @Test
    fun resolvesSpecificQuoteByKeyWhenWidgetCacheIsCold() = runBlocking {
        val dao = database.literaryClockDao()
        val moment = MomentEntity(key = 600)
        val targetQuote = QuoteEntity(
            key = "target-quote-key",
            quote = "target quote",
            title = "Target title",
            author = "Target author",
            asin = "TARGET-ASIN",
            momentKey = moment.key,
        )
        val otherQuote = QuoteEntity(
            key = "other-quote-key",
            quote = "other quote",
            title = "Other title",
            author = "Other author",
            asin = "OTHER-ASIN",
            momentKey = moment.key,
        )
        dao.upsertMoments(listOf(moment))
        dao.upsertQuotes(listOf(targetQuote, otherQuote))

        val resolvedQuote = LiteraryWidgetUpdater.resolveQuoteForWidget(
            context = context,
            quoteKey = targetQuote.key,
            isFavorite = true,
        )

        requireNotNull(resolvedQuote)
        assertEquals(targetQuote.key, resolvedQuote.key)
        assertEquals(targetQuote.quote, resolvedQuote.quote)
        assertEquals(targetQuote.title, resolvedQuote.title)
        assertEquals(targetQuote.author, resolvedQuote.author)
        assertEquals(targetQuote.asin, resolvedQuote.asin)
        assertTrue(resolvedQuote.isFavorite)
    }
}
