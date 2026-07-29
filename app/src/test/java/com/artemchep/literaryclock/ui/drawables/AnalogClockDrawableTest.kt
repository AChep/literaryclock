package com.artemchep.literaryclock.ui.drawables

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AnalogClockDrawableTest {

    @Test
    fun drawUsesCenterOfOffsetBounds() {
        val canvas = Canvas()
        val drawable = AnalogClockDrawable().apply {
            color = Color.BLACK
            setBounds(10, 20, 110, 120)
        }

        drawable.draw(canvas)

        val circle = shadowOf(canvas).getDrawnCircle(0)
        assertThat(circle.centerX).isEqualTo(60f)
        assertThat(circle.centerY).isEqualTo(70f)
        assertThat(circle.radius).isEqualTo(50f * 0.1f)
    }

    @Test
    fun drawRespectsDrawableAlphaAndColorFilter() {
        val canvas = Canvas()
        val colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
        val drawable = AnalogClockDrawable().apply {
            color = Color.BLACK
            alpha = 128
            setColorFilter(colorFilter)
            setBounds(0, 0, 100, 100)
        }

        drawable.draw(canvas)

        val shadowCanvas = shadowOf(canvas)
        assertThat(shadowCanvas.getDrawnLine(0).paint.alpha).isEqualTo(128)
        assertThat(shadowCanvas.getDrawnLine(0).paint.colorFilter).isSameInstanceAs(colorFilter)
        assertThat(shadowCanvas.getDrawnCircle(0).paint.alpha).isEqualTo(128)
        assertThat(shadowCanvas.getDrawnCircle(0).paint.colorFilter).isSameInstanceAs(colorFilter)
    }

    @Test
    fun getAlphaReturnsCurrentDrawableAlpha() {
        val drawable = AnalogClockDrawable()

        drawable.alpha = 128

        assertThat(drawable.alpha).isEqualTo(128)
    }

    @Test
    fun getColorFilterReturnsCurrentDrawableColorFilter() {
        val colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
        val drawable = AnalogClockDrawable()

        drawable.colorFilter = colorFilter

        assertThat(drawable.colorFilter).isSameInstanceAs(colorFilter)
    }
}
