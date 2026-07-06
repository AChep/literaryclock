package com.artemchep.literaryclock.ui.drawables

import android.graphics.Canvas
import android.graphics.Color
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
}
