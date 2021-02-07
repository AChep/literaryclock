package com.artemchep.literaryclock.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.checkout.intentstarters.FragmentIntentStarter
import com.artemchep.literaryclock.databinding.FragmentDonateBinding
import com.artemchep.literaryclock.logic.viewmodels.DonateViewModel
import com.artemchep.literaryclock.models.Loader
import com.artemchep.literaryclock.ui.items.SkuItem
import com.artemchep.literaryclock.utils.ext.setOnApplyWindowInsetsListener
import com.artemchep.literaryclock.utils.wrapInStatusBarView
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import org.solovyev.android.checkout.Inventory

/**
 * @author Artem Chepurnoy
 */
class DonateFragment : BaseFragment<FragmentDonateBinding>(), View.OnClickListener {

    override val viewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDonateBinding
        get() = FragmentDonateBinding::inflate

    private val donateViewModel: DonateViewModel by viewModels()

    private val itemAdapter by lazy { ItemAdapter<SkuItem>() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = super.onCreateView(inflater, container, savedInstanceState)
        .let {
            wrapInStatusBarView(it)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnApplyWindowInsetsListener { insets ->
            viewBinding.scrollView.updatePadding(
                bottom = insets.systemWindowInsetBottom,
                right = insets.systemWindowInsetRight,
                left = insets.systemWindowInsetLeft
            )
            viewBinding.navUpBtnContainer.updatePadding(
                right = insets.systemWindowInsetRight,
                left = insets.systemWindowInsetLeft
            )

            view.findViewById<View>(R.id.statusBarBg).apply {
                layoutParams.height = insets.systemWindowInsetTop
                requestLayout()
            }

            insets.consumeSystemWindowInsets()
        }

        viewBinding.navUpBtn.setOnClickListener(this)

        viewBinding.recyclerView.layoutManager = LinearLayoutManager(context)
        viewBinding.recyclerView.adapter = FastAdapter.with(itemAdapter).apply {
            onClickListener = object : ClickListener<SkuItem> {
                override fun invoke(
                    v: View?,
                    adapter: IAdapter<SkuItem>,
                    item: SkuItem,
                    position: Int
                ): Boolean {
                    val intentStarter = FragmentIntentStarter(this@DonateFragment)
                    donateViewModel.purchase(intentStarter, item.sku)

                    // We handled the click
                    return true
                }
            }
        }

        donateViewModel.setup()
    }

    private fun DonateViewModel.setup() {
        productLiveData.observe(viewLifecycleOwner, Observer(::showProducts))
    }

    private fun showProducts(products: Loader<Inventory.Products>) {
        when(products) {
            is Loader.Ok -> {
                viewBinding.errorView.isVisible = false
                viewBinding.progressView.isVisible = false
                viewBinding.recyclerView.isVisible = true

                // Bind products to recycler view.
                val items = products.value
                    .flatMap { product ->
                        product.skus
                            .map { sku ->
                                SkuItem(
                                    sku = sku,
                                    isPurchased = product.isPurchased(sku)
                                )
                            }
                    }
                    .sortedBy { sku -> sku.sku.detailedPrice.amount }

                itemAdapter.setNewList(items)
            }
            is Loader.Loading -> {
                viewBinding.errorView.isVisible = false
                viewBinding.progressView.isVisible = true
                viewBinding.recyclerView.isVisible = false
            }
            is Loader.Error -> {
                viewBinding.errorView.isVisible = true
                viewBinding.progressView.isVisible = false
                viewBinding.recyclerView.isVisible = false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        donateViewModel.result(requestCode, resultCode, data)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.navUpBtn -> findNavController().navigateUp()
        }
    }

}
