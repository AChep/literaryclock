package com.artemchep.literaryclock.store.factory

import android.content.Context
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuoteItemFactoryDreamAccentTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun spanifyUsesAccentOverrideForHighlightedQuoteSegment() {
        val accentColor = 0xFF123456.toInt()

        val spanned = QuoteItemFactory.spanify(
            context = context,
            quote = "Before <strong>12:34</strong> after",
            accentColor = accentColor,
        ) as Spanned

        val matchingSpan = spanned
            .getSpans(0, spanned.length, ForegroundColorSpan::class.java)
            .firstOrNull { it.foregroundColor == accentColor }

        requireNotNull(matchingSpan)
        val highlightedText = spanned.substring(
            spanned.getSpanStart(matchingSpan),
            spanned.getSpanEnd(matchingSpan),
        )
        assertEquals("12:34", highlightedText)
    }

    @Test
    fun spanifyKeepsDefaultAccentForExistingCallers() {
        val spanned = QuoteItemFactory.spanify(
            context = context,
            quote = "Before <strong>12:34</strong> after",
        ) as Spanned

        val colors = spanned
            .getSpans(0, spanned.length, ForegroundColorSpan::class.java)
            .map(ForegroundColorSpan::getForegroundColor)

        assertTrue(colors.contains(QuoteItemFactory.DEFAULT_PRIMARY_COLOR))
    }
}
