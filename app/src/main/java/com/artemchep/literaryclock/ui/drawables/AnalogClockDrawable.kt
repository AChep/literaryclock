package com.artemchep.literaryclock.ui.drawables

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.core.graphics.withRotation
import kotlin.math.max
import kotlin.math.min

/**
 * @author Artem Chepurnoy
 */
class AnalogClockDrawable : Drawable() {

    /** The rotation of the hour hand */
    var hourHandRotation: Float = 0f
        set(value) {
            field = value
            invalidateSelf()
        }

    /** The rotation of the minute hand */
    var minuteHandRotation: Float = 0f
        set(value) {
            field = value
            invalidateSelf()
        }

    private val paint = Paint()
        .apply {
            isAntiAlias = true
            strokeWidth = 8f
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        }

    /** The color of a clock */
    var color: Int
        get() = paint.color
        set(value) {
            paint.color = value
            paintDot.color = value
        }

    private val paintDot = Paint()
        .apply {
            isAntiAlias = true
        }

    override fun draw(canvas: Canvas) = canvas.performDraw()

    private fun Canvas.performDraw() {
        val centerX = bounds.exactCenterX() + bounds.left
        val centerY = bounds.exactCenterY() + bounds.top
        val radius = min(bounds.exactCenterX(), bounds.exactCenterY())

        // Draw hour hand
        val hourHandLength = (radius - paint.strokeWidth) * 0.4f
        drawClockHand(hourHandRotation, centerX, centerY, hourHandLength, paint)

        // Draw minute hand
        val minuteHandLength = (radius - paint.strokeWidth) * 0.75f
        drawClockHand(minuteHandRotation, centerX, centerY, minuteHandLength, paint)

        // Draw a dot in a center
        drawCircle(centerX, centerY, max(radius * 0.1f, paint.strokeWidth / 2f), paintDot)
    }

    private fun Canvas.drawClockHand(
        rotation: Float,
        centerX: Float,
        centerY: Float,
        length: Float,
        paint: Paint
    ) {
        withRotation(rotation, centerX, centerY) {
            drawLine(centerX, centerY, centerX, centerY - length, paint)
        }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        error("Unsupported")
    }

    override fun setAlpha(alpha: Int) {
        error("Unsupported")
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

}