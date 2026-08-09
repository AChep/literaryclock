package com.artemchep.literaryclock.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.artemchep.literaryclock.R
import com.google.common.truth.Truth.assertWithMessage
import org.json.JSONArray
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuoteMarkupTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun bundledQuotesHaveBalancedStrongMarkers() {
        val errors = listOf(
            R.raw.database to "database.json",
            R.raw.submissions to "submissions.json",
        ).flatMap { (resourceId, name) ->
            findUnbalancedStrongMarkers(name, readRawResource(resourceId))
        }

        assertWithMessage("Unbalanced strong markers in bundled quote data")
            .that(errors)
            .isEmpty()
    }

    @Test
    fun bundledDatabaseHighlightsKnownQuoteTime() {
        val quote = findQuoteByKey(
            jsonString = readRawResource(R.raw.database),
            key = "ebsdgM9MxNjlzKnqVTXo",
        )

        assertWithMessage("The 03:21 quote should highlight its 03:21 phrase")
            .that(quote.getString("quote"))
            .contains("<strong>twenty-one minutes past three</strong>")
    }

    @Test
    fun bundledDatabaseDoesNotReuseHighlightedQuoteForDifferentTimes() {
        val errors = findReusedHighlightedQuotes(readRawResource(R.raw.database))

        assertWithMessage("Bundled quote rows must not reuse the same highlighted text for different times")
            .that(errors)
            .isEmpty()
    }

    private fun findReusedHighlightedQuotes(jsonString: String): List<String> {
        val array = JSONArray(jsonString)
        return (0 until array.length())
            .asSequence()
            .map(array::getJSONObject)
            .groupBy { obj -> obj.getString("quote") }
            .filterValues { quotes ->
                quotes.map { quote -> quote.getInt("time") }.distinct().size > 1
            }
            .map { (quote, quotes) ->
                val keys = quotes.joinToString { row ->
                    "${row.getString("key")}@${row.getInt("time")}"
                }
                "$keys reuse ${quote.take(80)}"
            }
    }

    private fun findUnbalancedStrongMarkers(
        name: String,
        jsonString: String,
    ): List<String> {
        val array = JSONArray(jsonString)
        return (0 until array.length()).mapNotNull { index ->
            val obj = array.getJSONObject(index)
            val quote = obj.getString("quote")
            val openingCount = quote.windowedSequence("<strong>".length)
                .count { it == "<strong>" }
            val closingCount = quote.windowedSequence("</strong>".length)
                .count { it == "</strong>" }

            if (openingCount == closingCount) {
                null
            } else {
                "$name:${obj.optString("key", "#$index")} has $openingCount opening and $closingCount closing strong markers"
            }
        }
    }

    private fun findQuoteByKey(jsonString: String, key: String) =
        JSONArray(jsonString)
            .let { array ->
                (0 until array.length())
                    .asSequence()
                    .map(array::getJSONObject)
                    .first { obj -> obj.getString("key") == key }
            }

    private fun readRawResource(resourceId: Int): String =
        context.resources.openRawResource(resourceId)
            .bufferedReader()
            .use { it.readText() }
}
