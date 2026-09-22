package com.artemchep.literaryclock.ui.fragments

import android.content.Context
import android.content.Intent
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.test.core.app.ApplicationProvider
import com.artemchep.literaryclock.R
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainFragmentLayoutTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun moreButtonHasAccessibleLabel() {
        val themedContext = ContextThemeWrapper(context, R.style.AppTheme)
        val view = LayoutInflater.from(themedContext).inflate(R.layout.fragment_main, null)

        assertThat(
            view.findViewById<android.view.View>(R.id.moreBtn)
                .contentDescription
                ?.toString()
        )
            .isNotEmpty()
    }

    @Test
    fun shareQuoteUsesPlainTextMimeType() {
        val intent = createQuoteShareIntent(
            subject = "Literary Clock",
            text = "Quote text",
        )

        assertThat(intent.action).isEqualTo(Intent.ACTION_SEND)
        assertThat(intent.type).isEqualTo("text/plain")
        assertThat(intent.getStringExtra(Intent.EXTRA_SUBJECT)).isEqualTo("Literary Clock")
        assertThat(intent.getCharSequenceExtra(Intent.EXTRA_TEXT).toString()).isEqualTo("Quote text")
    }

    @Test
    fun clockHandRotationUsesShortestForwardPathAcrossZeroDegrees() {
        assertThat(calculateClockHandRotationDelta(new = 0f, old = 350f))
            .isEqualTo(10f)
    }

    @Test
    fun clockHandRotationUsesShortestBackwardPathAcrossZeroDegrees() {
        assertThat(calculateClockHandRotationDelta(new = 350f, old = 0f))
            .isEqualTo(-10f)
    }
}
