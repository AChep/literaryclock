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

    private fun readRawResource(resourceId: Int): String =
        context.resources.openRawResource(resourceId)
            .bufferedReader()
            .use { it.readText() }
}
