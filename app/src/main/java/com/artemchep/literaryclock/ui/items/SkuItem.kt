package com.artemchep.literaryclock.ui.items

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.databinding.ItemDonationBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.artemchep.literaryclock.billing.DonationProduct
import com.artemchep.literaryclock.billing.DonationState

/**
 * @author Artem Chepurnoy
 */
class SkuItem(
    val product: DonationProduct,
) : AbstractItem<SkuItem.ViewHolder>() {
    override val layoutRes: Int
        get() = R.layout.item_donation

    override val type: Int
        get() = 0

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(view: View) : FastAdapter.ViewHolder<SkuItem>(view) {

        private val viewBinding = ItemDonationBinding.bind(view)

        override fun bindView(item: SkuItem, payloads: List<Any>) {
            val product = item.product
            val purchased = product.state == DonationState.PURCHASED
            viewBinding.priceTextView.text = if (product.state == DonationState.PENDING) {
                itemView.context.getString(R.string.donation_pending)
            } else {
                product.formattedPrice
            }
            viewBinding.priceTextView.isEnabled = product.canPurchase
            viewBinding.priceTextView.isGone = purchased
            viewBinding.purchasedTextView.isVisible = purchased
            viewBinding.titleTextView.text = product.title
            viewBinding.summaryTextView.text = product.description
        }

        override fun unbindView(item: SkuItem) {
        }

    }

}