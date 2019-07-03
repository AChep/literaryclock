package com.artemchep.literaryclock.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.checkout.intentstarters.FragmentIntentStarter
import com.artemchep.literaryclock.logic.viewmodels.DonateViewModel
import com.artemchep.literaryclock.models.Loader
import com.artemchep.literaryclock.ui.items.SkuItem
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.android.synthetic.main.fragment_donate.*
import org.solovyev.android.checkout.Inventory

/**
 * @author Artem Chepurnoy
 */
class DonateFragment : BaseFragment(), View.OnClickListener {

    private lateinit var donateViewModel: DonateViewModel

    private val itemAdapter by lazy { ItemAdapter<SkuItem>() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_donate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navUpBtn.setOnClickListener(this)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = FastAdapter.with<SkuItem, ItemAdapter<*>>(itemAdapter)
            .withOnClickListener { _, _, item, _ ->
                val intentStarter = FragmentIntentStarter(this)
                donateViewModel.purchase(intentStarter, item.sku)

                // We handled the click
                true
            }

        donateViewModel = ViewModelProviders.of(this).get(DonateViewModel::class.java)
        donateViewModel.setup()
    }

    private fun DonateViewModel.setup() {
        productLiveData.observe(viewLifecycleOwner, Observer(::showProducts))
    }

    private fun showProducts(products: Loader<Inventory.Products>) {
        when(products) {
            is Loader.Ok -> {
                errorView.isVisible = false
                progressView.isVisible = false
                recyclerView.isVisible = true

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
                errorView.isVisible = false
                progressView.isVisible = true
                recyclerView.isVisible = false
            }
            is Loader.Error -> {
                errorView.isVisible = true
                progressView.isVisible = false
                recyclerView.isVisible = false
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
