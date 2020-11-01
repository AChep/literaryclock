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
import kotlinx.android.synthetic.main.fragment_donate_quote.*

/**
 * @author Artem Chepurnoy
 */
class DonateQuoteFragment : BaseFragment(), View.OnClickListener {

    private val donateQuoteViewModel: DonateQuoteViewModel by viewModels()

    private lateinit var adapter: QuoteAdapter

    /** The format of a digital clock used system-wide */
    private val timeFormat by lazy { createTimeFormat(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_donate_quote, container, false)
            .let {
                wrapInStatusBarView(it)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnApplyWindowInsetsListener { insets ->
            scrollView.updatePadding(
                bottom = insets.systemWindowInsetBottom,
                right = insets.systemWindowInsetRight,
                left = insets.systemWindowInsetLeft
            )
            navUpBtnContainer.updatePadding(
                right = insets.systemWindowInsetRight,
                left = insets.systemWindowInsetLeft
            )

            view.findViewById<View>(R.id.statusBarBg).apply {
                layoutParams.height = insets.systemWindowInsetTop
                requestLayout()
            }

            insets.consumeSystemWindowInsets()
        }

        navUpBtn.setOnClickListener(this)
        btn.setOnClickListener(this)
        timeTextView.setOnClickListener(this)

        quoteInfoInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                donateQuoteViewModel.postText(s.toString())
            }
        })

        quotesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        quotesRecyclerView.adapter = QuoteAdapter(
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
            requireContext().showTimePickerDialog(time, donateQuoteViewModel::postTime)
        })

        textLiveData.observe(viewLifecycleOwner, Observer(::showText))
        timeLiveData.observe(viewLifecycleOwner, Observer(::showTime))
        quoteLiveData.observe(viewLifecycleOwner, Observer(::showQuote))
        momentLiveData.observe(viewLifecycleOwner, Observer(::showMoment))
        databaseIsUpdatingLiveData.observe(viewLifecycleOwner, Observer(::showDatabaseState))
    }

    private fun showQuote(quote: QuoteItem) {
        quoteExampleTextView.text = """
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
        if (quoteInfoInputEditText.text?.length ?: 0 != text.length) {
            quoteInfoInputEditText.setText(text)
        }
    }

    private fun showTime(time: Time) {
        timeTextView.text = formatTime(time, timeFormat)
    }

    private fun showDatabaseState(state: DatabaseState) =
        progressBar.setProgressBarShown(state.isWorking)

    override fun onClick(view: View) {
        when (view.id) {
            R.id.navUpBtn -> navigateUp()
            R.id.btn -> donateQuoteViewModel.send()
            R.id.timeTextView -> donateQuoteViewModel.editTime()
        }
    }

    private fun navigateUp() = findNavController().navigateUp()

}
