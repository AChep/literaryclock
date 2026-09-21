package com.artemchep.literaryclock.widget

import android.content.Context
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.artemchep.literaryclock.R
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.xmlpull.v1.XmlPullParser

@RunWith(RobolectricTestRunner::class)
class LiteraryWidgetProviderMetadataTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun previewLayoutShowsQuoteAtWidgetMinimumSize() {
        val metadata = readWidgetMetadata()
        val preview = LayoutInflater.from(context).inflate(metadata.previewLayout, null)

        val width = View.MeasureSpec.makeMeasureSpec(metadata.minWidth, View.MeasureSpec.EXACTLY)
        val height = View.MeasureSpec.makeMeasureSpec(metadata.minHeight, View.MeasureSpec.EXACTLY)
        preview.measure(width, height)
        preview.layout(0, 0, preview.measuredWidth, preview.measuredHeight)

        val quoteView = preview.findViewById<TextView>(R.id.quoteTextView)
        assertThat(quoteView.text.toString()).isNotEmpty()
        assertThat(quoteView.height).isGreaterThan(0)
    }

    private fun readWidgetMetadata(): WidgetMetadata {
        val parser = context.resources.getXml(R.xml.widget)
        while (parser.next() != XmlPullParser.START_TAG) {
            // Skip the XML declaration.
        }
        val attrs = Xml.asAttributeSet(parser)
        val typedArray = context.obtainStyledAttributes(
            attrs,
            intArrayOf(
                android.R.attr.minWidth,
                android.R.attr.minHeight,
                android.R.attr.previewLayout,
            ),
        )

        return try {
            WidgetMetadata(
                minWidth = typedArray.getDimensionPixelSize(0, 0),
                minHeight = typedArray.getDimensionPixelSize(1, 0),
                previewLayout = typedArray.getResourceId(2, 0),
            )
        } finally {
            typedArray.recycle()
        }
    }

    private data class WidgetMetadata(
        val minWidth: Int,
        val minHeight: Int,
        val previewLayout: Int,
    )
}
