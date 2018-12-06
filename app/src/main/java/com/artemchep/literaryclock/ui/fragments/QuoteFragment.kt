package com.artemchep.literaryclock.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.artemchep.literaryclock.R
import kotlinx.android.synthetic.main.fragment_quote.*

/**
 * @author Artem Chepurnoy
 */
class QuoteFragment : Fragment() {

    val args by lazy { QuoteFragmentArgs.fromBundle(arguments!!) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quote, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quoteTextView.text = args.quote.quote
    }

}
