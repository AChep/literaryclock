package com.artemchep.literaryclock.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class DatabaseImporterTest {
    private lateinit var database: LiteraryClockDatabase
    private lateinit var dao: LiteraryClockDao
    private lateinit var importer: DatabaseImporter

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            LiteraryClockDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
        dao = database.literaryClockDao()
        importer = DatabaseImporter(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun importJsonReplacesStaleRowsAndUpsertsFreshOnes() = runTest {
        dao.upsertMoments(
            listOf(
                MomentEntity(key = 1),
                MomentEntity(key = 3),
            ),
        )
        dao.upsertQuotes(
            listOf(
                QuoteEntity(
                    key = "quote-1",
                    quote = "old quote",
                    title = "old title",
                    author = "old author",
                    asin = "OLD-ASIN",
                    momentKey = 1,
                ),
                QuoteEntity(
                    key = "stale-quote",
                    quote = "stale quote",
                    title = "stale title",
                    author = "stale author",
                    asin = "STALE-ASIN",
                    momentKey = 3,
                ),
            ),
        )

        importer.importJson(
            quotesJson(
                quote(
                    key = "quote-1",
                    time = 1,
                    quote = "fresh quote",
                    title = "fresh title",
                    author = "fresh author",
                    asin = "FRESH-ASIN",
                ),
                quote(
                    key = "quote-2",
                    time = 2,
                    quote = "second quote",
                    title = "second title",
                    author = "second author",
                    asin = "SECOND-ASIN",
                ),
            ),
        )

        assertThat(dao.getAllMomentKeys()).containsExactly(1, 2)
        assertThat(dao.getAllQuoteKeys()).containsExactly("quote-1", "quote-2")
        assertThat(dao.getQuoteByKey("stale-quote")).isNull()

        val updatedQuote = requireNotNull(dao.getQuoteByKey("quote-1"))
        assertThat(updatedQuote.quote).isEqualTo("fresh quote")
        assertThat(updatedQuote.title).isEqualTo("fresh title")
        assertThat(updatedQuote.author).isEqualTo("fresh author")
        assertThat(updatedQuote.asin).isEqualTo("FRESH-ASIN")
        assertThat(updatedQuote.momentKey).isEqualTo(1)
    }

    @Test
    fun importJsonClearsDatabaseWhenPayloadIsEmpty() = runTest {
        dao.upsertMoments(listOf(MomentEntity(key = 42)))
        dao.upsertQuotes(
            listOf(
                QuoteEntity(
                    key = "quote-42",
                    quote = "quote",
                    title = "title",
                    author = "author",
                    asin = "ASIN-42",
                    momentKey = 42,
                ),
            ),
        )

        importer.importJson("[]")

        assertThat(dao.getAllMomentKeys()).isEmpty()
        assertThat(dao.getAllQuoteKeys()).isEmpty()
    }

    @Test
    fun importJsonDeletesLargeStaleSetsInChunks() = runTest {
        val staleMoments = (0..900).map { index ->
            MomentEntity(key = index)
        }
        val staleQuotes = staleMoments.map { moment ->
            QuoteEntity(
                key = "stale-${moment.key}",
                quote = "quote-${moment.key}",
                title = "title-${moment.key}",
                author = "author-${moment.key}",
                asin = "ASIN-${moment.key}",
                momentKey = moment.key,
            )
        }
        dao.upsertMoments(staleMoments)
        dao.upsertQuotes(staleQuotes)

        importer.importJson(
            quotesJson(
                quote(
                    key = "fresh-2000",
                    time = 2000,
                    quote = "fresh quote",
                    title = "fresh title",
                    author = "fresh author",
                    asin = "FRESH-ASIN",
                ),
            ),
        )

        assertThat(dao.getAllMomentKeys()).containsExactly(2000)
        assertThat(dao.getAllQuoteKeys()).containsExactly("fresh-2000")
        val freshQuote = requireNotNull(dao.getQuoteByKey("fresh-2000"))
        assertThat(freshQuote.momentKey).isEqualTo(2000)
    }

    private fun quotesJson(vararg quotes: String): String = buildString {
        append("[")
        append(quotes.joinToString(separator = ","))
        append("]")
    }

    private fun quote(
        key: String,
        time: Int,
        quote: String,
        title: String,
        author: String,
        asin: String,
    ): String = """
        {
          "key": "$key",
          "quote": "$quote",
          "title": "$title",
          "author": "$author",
          "asin": "$asin",
          "time": $time
        }
    """.trimIndent()
}
