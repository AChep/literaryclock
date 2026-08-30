package com.artemchep.literaryclock.ui.fragments

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.test.core.app.ApplicationProvider
import com.artemchep.literaryclock.R
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsFragmentLayoutTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun widgetUpdaterSwitchHasVisibleAccessibilityLabel() {
        val themedContext = ContextThemeWrapper(context, R.style.AppTheme)
        val view = LayoutInflater.from(themedContext).inflate(R.layout.fragment_settings, null)

        assertThat(view.findViewById<android.view.View>(R.id.altWidgetUpdaterTextView).labelFor)
            .isEqualTo(R.id.altWidgetUpdaterSwitch)
    }
}
