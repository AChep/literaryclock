package com.artemchep.literaryclock.ui

import android.content.Context
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import com.artemchep.literaryclock.R
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class ThemeResourceTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    @Config(sdk = [28])
    fun api28UsesPreQNavigationBarColor() {
        val themedContext = ContextThemeWrapper(context, R.style.AppTheme)
        val typedValue = TypedValue()

        val resolved = themedContext.theme.resolveAttribute(
            android.R.attr.navigationBarColor,
            typedValue,
            true
        )

        assertThat(resolved).isTrue()
        assertThat(typedValue.data)
            .isEqualTo(ContextCompat.getColor(context, R.color.colorNavigationBarPreQ))
    }
}
