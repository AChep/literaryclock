package com.artemchep.literaryclock.ui.fragments

import android.content.Intent
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FavoritesFragmentLayoutTest {
    @Test
    fun shareQuoteUsesPlainTextMimeType() {
        val intent = createFavoriteQuoteShareIntent(
            subject = "Literary Clock",
            text = "Quote text",
        )

        assertThat(intent.action).isEqualTo(Intent.ACTION_SEND)
        assertThat(intent.type).isEqualTo("text/plain")
        assertThat(intent.getStringExtra(Intent.EXTRA_SUBJECT)).isEqualTo("Literary Clock")
        assertThat(intent.getCharSequenceExtra(Intent.EXTRA_TEXT).toString()).isEqualTo("Quote text")
    }
}
