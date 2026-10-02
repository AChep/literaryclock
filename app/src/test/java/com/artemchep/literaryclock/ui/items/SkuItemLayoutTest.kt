package com.artemchep.literaryclock.ui.items

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.billing.DonationState
import com.artemchep.literaryclock.test.testDonationProduct
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SkuItemLayoutTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    private fun inflateItem(): View = LayoutInflater.from(ContextThemeWrapper(context, R.style.AppTheme))
        .inflate(R.layout.item_donation, null)

    @Test
    fun priceLabelDoesNotHandleClicksSeparatelyFromDonationRow() {
        val priceLabel = inflateItem().findViewById<View>(R.id.priceTextView)

        assertThat(priceLabel.isClickable).isFalse()
        assertThat(priceLabel.isFocusable).isFalse()
    }

    @Test
    fun pendingAndPurchasedStatesRenderCorrectlyWhenHolderIsReused() {
        val view = inflateItem()
        val product = testDonationProduct(formattedPrice = "€1.00")
        val holder = SkuItem(product).getViewHolder(view)
        val price = view.findViewById<TextView>(R.id.priceTextView)
        val purchased = view.findViewById<View>(R.id.purchasedTextView)

        holder.bindView(SkuItem(product.copy(state = DonationState.PENDING, canPurchase = false)), emptyList())
        assertThat(price.text.toString()).isEqualTo(context.getString(R.string.donation_pending))
        assertThat(price.isEnabled).isFalse()
        assertThat(purchased.visibility).isEqualTo(View.GONE)

        holder.bindView(SkuItem(product.copy(state = DonationState.PURCHASED, canPurchase = false)), emptyList())
        assertThat(price.visibility).isEqualTo(View.GONE)
        assertThat(purchased.visibility).isEqualTo(View.VISIBLE)

        holder.bindView(SkuItem(product), emptyList())
        assertThat(price.visibility).isEqualTo(View.VISIBLE)
        assertThat(price.isEnabled).isTrue()
        assertThat(price.text.toString()).isEqualTo("€1.00")
        assertThat(purchased.visibility).isEqualTo(View.GONE)
    }
}
