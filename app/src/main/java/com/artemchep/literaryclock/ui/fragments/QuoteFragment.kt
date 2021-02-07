package com.artemchep.literaryclock.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.artemchep.literaryclock.databinding.FragmentQuoteBinding
import com.artemchep.literaryclock.utils.wrapInStatusBarView

/**
 * @author Artem Chepurnoy
 */
class QuoteFragment : BaseFragment<FragmentQuoteBinding>() {

    override val viewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> FragmentQuoteBinding
        get() = FragmentQuoteBinding::inflate

    val args by lazy { QuoteFragmentArgs.fromBundle(requireArguments()) }

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
        viewBinding.quoteTextView.text = args.quote.quote
    }

}
