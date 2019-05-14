package com.artemchep.literaryclock.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.ui.interfaces.OnItemClickListener

/**
 * @author Artem Chepurnoy
 */
class QuoteAdapter(
    private val isClickable: Boolean = true,
    private val isShareBtnVisible: Boolean = true
) : AdapterBase<QuoteItem, QuoteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_quote, parent, false)
        return ViewHolder(view, if (isClickable) this else null, isShareBtnVisible)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = this[position]
        holder.apply {
            quoteTextView.text = model.quote
            titleTextView.text = model.title
            authorTextView.text = model.author
        }
    }

    /**
     * @author Artem Chepurnoy
     */
    class ViewHolder(
        view: View,
        listener: OnItemClickListener<Int>?,
        isShareBtnVisible: Boolean
    ) : AdapterBase.ViewHolderBase(view, listener) {
        val quoteTextView: TextView = view.findViewById(R.id.quoteTextView)
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val authorTextView: TextView = view.findViewById(R.id.authorTextView)
        val shareBtn: View = view.findViewById(R.id.shareBtn)

        init {
            // Bind listener only if we want to observe
            // on item click events.
            if (listener != null) {
                view.setOnClickListener(this)
                shareBtn.setOnClickListener(this)
            }

            shareBtn.isVisible = isShareBtnVisible
        }
    }

}
