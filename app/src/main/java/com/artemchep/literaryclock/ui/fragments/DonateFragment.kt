package com.artemchep.literaryclock.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.logic.viewmodels.DonateViewModel
import kotlinx.android.synthetic.main.fragment_about.*
import org.solovyev.android.checkout.Inventory

/**
 * @author Artem Chepurnoy
 */
class DonateFragment : Fragment(), View.OnClickListener {

    private lateinit var donateViewModel: DonateViewModel

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

        donateViewModel = ViewModelProviders.of(this).get(DonateViewModel::class.java)
        donateViewModel.setup()
    }

    private fun DonateViewModel.setup() {
        checkoutLiveData.observe(viewLifecycleOwner, Observer { /* unused */ })
        productLiveData.observe(viewLifecycleOwner, Observer(::showProducts))
    }

    private fun showProducts(products: Inventory.Products) {
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.navUpBtn -> findNavController().navigateUp()
        }
    }

}
