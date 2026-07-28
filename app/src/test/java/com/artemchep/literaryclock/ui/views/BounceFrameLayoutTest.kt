package com.artemchep.literaryclock.ui.views

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowView

@RunWith(RobolectricTestRunner::class)
class BounceFrameLayoutTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun bounceUsesUpdatedChildMeasurementForPositioning() {
        ShadowView.setUseRealViewAnimations(false)
        val parent = BounceFrameLayout(context)
        val child = ImmediatePostView(context)
        parent.addView(child, FrameLayout.LayoutParams(0, 100))
        parent.measure(
            View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY),
        )
        parent.layout(0, 0, 500, 500)

        parent.bounce {
            child.layoutParams = FrameLayout.LayoutParams(500, 100)
        }
        child.animate().runPendingEndAction()

        assertThat(child.translationX).isEqualTo(0f)

        parent.measure(
            View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY),
        )
        parent.layout(0, 0, 500, 500)

        assertThat(child.translationX).isEqualTo(0f)
    }

    private class ImmediatePostView(context: Context) : View(context) {
        override fun post(action: Runnable): Boolean {
            action.run()
            return true
        }
    }

    private fun android.view.ViewPropertyAnimator.runPendingEndAction() {
        val field = javaClass.getDeclaredField("mPendingOnEndAction")
        field.isAccessible = true
        val action = field.get(this) as Runnable
        action.run()
    }
}
