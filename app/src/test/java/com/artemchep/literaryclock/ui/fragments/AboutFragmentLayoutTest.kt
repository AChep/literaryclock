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
class AboutFragmentLayoutTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun socialButtonsHaveAccessibleLabels() {
        val themedContext = ContextThemeWrapper(context, R.style.AppTheme)
        val view = LayoutInflater.from(themedContext).inflate(R.layout.fragment_about, null)

        val socialButtonIds = listOf(
            R.id.linkedInBtn,
            R.id.twitterBtn,
            R.id.instagramBtn,
        )

        socialButtonIds.forEach { id ->
            assertThat(
                view.findViewById<android.view.View>(id)
                    .contentDescription
                    ?.toString()
            )
                .isNotEmpty()
        }
    }

    @Test
    fun shareAppUsesPlainTextMimeType() {
        val intent = createAppShareIntent(
            subject = "Literary Clock",
            text = "Check it out",
        )

        assertThat(intent.action).isEqualTo(Intent.ACTION_SEND)
        assertThat(intent.type).isEqualTo("text/plain")
        assertThat(intent.getStringExtra(Intent.EXTRA_SUBJECT)).isEqualTo("Literary Clock")
        assertThat(intent.getCharSequenceExtra(Intent.EXTRA_TEXT).toString()).isEqualTo("Check it out")
    }
}
