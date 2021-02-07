package com.artemchep.literaryclock.ui.items

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.databinding.ItemDonationBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import org.solovyev.android.checkout.Sku

/**
 * @author Artem Chepurnoy
 */
class SkuItem(
    val sku: Sku,
    val isPurchased: Boolean
) : AbstractItem<SkuItem.ViewHolder>() {
    override val layoutRes: Int
        get() = R.layout.item_donation

    override val type: Int
        get() = 0

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(view: View) : FastAdapter.ViewHolder<SkuItem>(view) {

        private val viewBinding = ItemDonationBinding.bind(view)

        override fun bindView(item: SkuItem, payloads: List<Any>) {
            viewBinding.priceTextView.text = item.sku.price
            viewBinding.priceTextView.isGone = item.isPurchased
            viewBinding.purchasedTextView.isVisible = item.isPurchased
            viewBinding.titleTextView.text = item.sku.displayTitle
            viewBinding.summaryTextView.text = item.sku.description
        }

        override fun unbindView(item: SkuItem) {
        }

    }

}