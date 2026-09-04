package com.artemchep.literaryclock.ui.fragments

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.test.core.app.ApplicationProvider
import com.artemchep.literaryclock.R
import com.google.android.material.button.MaterialButton
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DonateQuoteFragmentLayoutTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun sendButtonIconMirrorsInRtl() {
        val themedContext = ContextThemeWrapper(context, R.style.AppTheme)
        val view = LayoutInflater.from(themedContext).inflate(R.layout.fragment_donate_quote, null)

        assertThat(view.findViewById<MaterialButton>(R.id.btn).icon.isAutoMirrored)
            .isTrue()
    }
}
