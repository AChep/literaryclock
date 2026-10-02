package com.artemchep.literaryclock.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.literaryclock.billing.DonationProduct
import com.artemchep.literaryclock.databinding.FragmentDonateBinding
import com.artemchep.literaryclock.logic.viewmodels.DonateViewModel
import com.artemchep.literaryclock.models.Loader
import com.artemchep.literaryclock.ui.items.SkuItem
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter

/**
 * @author Artem Chepurnoy
 */
class DonateFragment : BaseFragment<FragmentDonateBinding>() {

    override val viewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDonateBinding
        get() = FragmentDonateBinding::inflate

    private val donateViewModel: DonateViewModel by viewModels()

    private val itemAdapter by lazy { ItemAdapter<SkuItem>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(context)
        viewBinding.recyclerView.adapter = FastAdapter.with(itemAdapter).apply {
            onClickListener = object : ClickListener<SkuItem> {
                override fun invoke(
                    v: View?,
                    adapter: IAdapter<SkuItem>,
                    item: SkuItem,
                    position: Int
                ): Boolean {
                    if (item.product.canPurchase) {
                        donateViewModel.purchase(requireActivity(), item.product)
                    }

                    // We handled the click
                    return true
                }
            }
        }

        donateViewModel.setup()
        viewBinding.errorView.setOnClickListener { donateViewModel.refresh() }
    }

    override fun onResume() {
        super.onResume()
        donateViewModel.refresh()
    }

    private fun DonateViewModel.setup() {
        productLiveData.observe(viewLifecycleOwner, Observer(::showProducts))
    }

    private fun showProducts(products: Loader<List<DonationProduct>>) {
        when (products) {
            is Loader.Ok -> {
                viewBinding.errorView.isVisible = false
                viewBinding.progressView.isVisible = false
                viewBinding.recyclerView.isVisible = true

                // Bind products to recycler view.
                val items = products.value.map(::SkuItem)

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

}
