package com.artemchep.literaryclock.ui.items

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
class SkuItemLayoutTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun priceLabelDoesNotHandleClicksSeparatelyFromDonationRow() {
        val themedContext = ContextThemeWrapper(context, R.style.AppTheme)
        val view = LayoutInflater.from(themedContext).inflate(R.layout.item_donation, null)
        val priceLabel = view.findViewById<android.view.View>(R.id.priceTextView)

        assertThat(priceLabel.isClickable).isFalse()
        assertThat(priceLabel.isFocusable).isFalse()
    }
}
