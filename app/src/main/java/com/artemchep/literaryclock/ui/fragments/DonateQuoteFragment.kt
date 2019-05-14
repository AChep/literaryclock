package com.artemchep.literaryclock.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
import com.artemchep.literaryclock.utils.formatTime
import kotlinx.android.synthetic.main.fragment_donate_quote.*

/**
 * @author Artem Chepurnoy
 */
class DonateQuoteFragment : Fragment(), View.OnClickListener {

    private lateinit var donateQuoteViewModel: DonateQuoteViewModel

    private lateinit var adapter: QuoteAdapter

    /** The format of a digital clock used system-wide */
    private val timeFormat by lazy { createTimeFormat(context!!) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_donate_quote, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        quotesRecyclerView.layoutManager = LinearLayoutManager(context!!)
        quotesRecyclerView.adapter = QuoteAdapter(
            isClickable = false,
            isShareBtnVisible = false
        )
            .also(::adapter::set)

        donateQuoteViewModel = ViewModelProviders.of(this).get(DonateQuoteViewModel::class.java)
        donateQuoteViewModel.setup()
    }

    private fun DonateQuoteViewModel.setup() {
        popEvent.observe(viewLifecycleOwner, Observer {
            navigateUp()
        })

        editTimeEvent.observe(viewLifecycleOwner, Observer { time ->
            context!!.showTimePickerDialog(time, donateQuoteViewModel::postTime)
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
