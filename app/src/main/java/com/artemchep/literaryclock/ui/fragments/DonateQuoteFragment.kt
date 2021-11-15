package com.artemchep.literaryclock.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.data.DatabaseState
import com.artemchep.literaryclock.databinding.FragmentDonateQuoteBinding
import com.artemchep.literaryclock.logic.viewmodels.DonateQuoteViewModel
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.ui.adapters.QuoteAdapter
import com.artemchep.literaryclock.ui.setProgressBarShown
import com.artemchep.literaryclock.ui.showTimePickerDialog
import com.artemchep.literaryclock.utils.createTimeFormat
import com.artemchep.literaryclock.utils.ext.setOnApplyWindowInsetsListener
import com.artemchep.literaryclock.utils.formatTime
import com.artemchep.literaryclock.utils.wrapInStatusBarView

/**
 * @author Artem Chepurnoy
 */
class DonateQuoteFragment : BaseFragment<FragmentDonateQuoteBinding>(), View.OnClickListener {

    override val viewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDonateQuoteBinding
        get() = FragmentDonateQuoteBinding::inflate

    private val donateQuoteViewModel: DonateQuoteViewModel by viewModels()

    private lateinit var adapter: QuoteAdapter

    /** The format of a digital clock used system-wide */
    private val timeFormat by lazy { createTimeFormat(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        viewBinding.btn.setOnClickListener(this)
        viewBinding.timeTextView.setOnClickListener(this)

        viewBinding.quoteInfoInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                donateQuoteViewModel.postText(s.toString())
            }
        })

        viewBinding.quotesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.quotesRecyclerView.adapter = QuoteAdapter(
            isClickable = false,
            isShareBtnVisible = false
        )
            .also(::adapter::set)

        donateQuoteViewModel.setup()
    }

    private fun DonateQuoteViewModel.setup() {
        popEvent.observe(viewLifecycleOwner, Observer {
            navigateUp()
        })

        editTimeEvent.observe(viewLifecycleOwner, Observer { time ->
            parentFragmentManager.showTimePickerDialog(time, donateQuoteViewModel::postTime)
        })

        textLiveData.observe(viewLifecycleOwner, Observer(::showText))
        timeLiveData.observe(viewLifecycleOwner, Observer(::showTime))
        quoteLiveData.observe(viewLifecycleOwner, Observer(::showQuote))
        momentLiveData.observe(viewLifecycleOwner, Observer(::showMoment))
        databaseIsUpdatingLiveData.observe(viewLifecycleOwner, Observer(::showDatabaseState))
    }

    private fun showQuote(quote: QuoteItem) {
        viewBinding.quoteExampleTextView.text = """
            ${quote.quote}

            ${quote.title}
            ${quote.author}
        """.trimIndent()
    }

    private fun showMoment(moment: MomentItem) {
        adapter.apply {
            models.apply {
                clear()
                addAll(moment.quotes)
            }

            notifyDataSetChanged()
        }
    }

    private fun showText(text: String) {
        if (viewBinding.quoteInfoInputEditText.text?.length ?: 0 != text.length) {
            viewBinding.quoteInfoInputEditText.setText(text)
        }
    }

    private fun showTime(time: Time) {
        viewBinding.timeTextView.text = formatTime(time, timeFormat)
    }

    private fun showDatabaseState(state: DatabaseState) =
        viewBinding.progressBar.setProgressBarShown(state.isWorking)

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn -> donateQuoteViewModel.send()
            R.id.timeTextView -> donateQuoteViewModel.editTime()
        }
    }

    private fun navigateUp() = findNavController().navigateUp()

}
