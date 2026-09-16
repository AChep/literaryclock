package com.artemchep.literaryclock.widget

import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.ui.activities.MainActivity
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class LiteraryWidgetUpdaterTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun quoteTextHasHeightAtWidgetMinimumSize() {
        val remoteViews = createRemoteViews(
            quote = QuoteItem(
                key = "quote-key",
                quote = "Quote text",
                title = "Quote title",
                asin = "ASIN",
                author = "Author",
            ),
        )
        val widgetView = remoteViews.apply(context, null)

        val width = View.MeasureSpec.makeMeasureSpec(140.dp, View.MeasureSpec.EXACTLY)
        val height = View.MeasureSpec.makeMeasureSpec(110.dp, View.MeasureSpec.EXACTLY)
        widgetView.measure(width, height)
        widgetView.layout(0, 0, widgetView.measuredWidth, widgetView.measuredHeight)

        val quoteView = widgetView.findViewById<TextView>(R.id.quoteTextView)
        assertThat(quoteView.text.toString()).contains("Quote text")
        assertThat(quoteView.height).isGreaterThan(0)
    }

    @Test
    fun quoteClickLaunchesMainActivityInNewTask() {
        val remoteViews = createRemoteViews(
            quote = QuoteItem(
                key = "quote-key",
                quote = "Quote text",
                title = "Quote title",
                asin = "ASIN",
                author = "Author",
            ),
        )
        val widgetView = remoteViews.apply(context, null)

        widgetView.findViewById<android.view.View>(R.id.quoteTextView).performClick()

        val startedIntent = shadowOf(context as Application).nextStartedActivity
        assertThat(startedIntent.component?.className).isEqualTo(MainActivity::class.java.name)
        assertThat(startedIntent.flags and Intent.FLAG_ACTIVITY_NEW_TASK)
            .isEqualTo(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    @Suppress("UNCHECKED_CAST")
    private fun createRemoteViews(quote: QuoteItem): RemoteViews {
        val method = LiteraryWidgetUpdater::class.java.getDeclaredMethod(
            "createLiteraryRemoteViews",
            Context::class.java,
            QuoteItem::class.java,
        )
        method.isAccessible = true
        return method.invoke(LiteraryWidgetUpdater, context, quote) as RemoteViews
    }

    private val Int.dp: Int
        get() = (this * context.resources.displayMetrics.density).toInt()
}
