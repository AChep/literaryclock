package com.artemchep.literaryclock.ui.fragments

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.test.core.app.ApplicationProvider
import com.artemchep.literaryclock.R
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
class DonateFragmentLayoutTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun loadingIndicatorIsCenteredInVisibleScrollViewport() {
        val themedContext = ContextThemeWrapper(context, R.style.AppTheme)
        val view = LayoutInflater.from(themedContext).inflate(R.layout.fragment_donate, null)

        view.findViewById<View>(R.id.recyclerView).isVisible = false
        view.findViewById<View>(R.id.errorView).isVisible = false
        val progressView = view.findViewById<View>(R.id.progressView)
        progressView.isVisible = true

        val width = View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY)
        val height = View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        view.measure(width, height)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val scrollView = view.findViewById<NestedScrollView>(R.id.scrollView)
        val scrollContent = scrollView.getChildAt(0)
        val progressCenter = progressView.top + progressView.height / 2
        val contentCenter = scrollContent.height / 2

        assertThat(scrollContent.height)
            .isAtLeast(scrollView.height)
        assertThat(abs(progressCenter - contentCenter))
            .isAtMost(1)
    }
}
