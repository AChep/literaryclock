package com.artemchep.literaryclock.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.children
import kotlin.random.Random

/**
 * @author Artem Chepurnoy
 */
class BounceFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val ANIMATION_DURATION = 200L
    }

    /**
     * Plays fade-out animation, invokes the block and
     * plays fade-in animation.
     */
    inline fun bounce(crossinline block: () -> Unit) {
        val child = children.firstOrNull() ?: error("Bounce layout should have one child.")
        child.animate()
            .alpha(0f)
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(ANIMATION_DURATION)
            .withEndAction {
                // Perform some actions on the
                // child view.
                block.invoke()

                child.requestLayout()
                child.post {
                    child.translationX = (width - child.measuredWidth) * Random.nextFloat()
                    child.translationY = (height - child.measuredHeight) * Random.nextFloat()
                    child.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .duration = ANIMATION_DURATION
                }
            }
    }

}