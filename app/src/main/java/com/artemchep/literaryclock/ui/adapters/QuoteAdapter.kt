package com.artemchep.literaryclock.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.core.view.isVisible
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.ui.interfaces.OnItemClickListener

/**
 * @author Artem Chepurnoy
 */
class QuoteAdapter(
    private val isClickable: Boolean = true,
    private val isShareBtnVisible: Boolean = true,
    private val isFavoriteBtnVisible: Boolean = true,
) : AdapterBase<QuoteItem, QuoteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_quote, parent, false)
        return ViewHolder(
            view = view,
            listener = if (isClickable) this else null,
            isShareBtnVisible = isShareBtnVisible,
            isFavoriteBtnVisible = isFavoriteBtnVisible,
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = this[position]
        holder.bind(model)
    }

    /**
     * @author Artem Chepurnoy
     */
    class ViewHolder(
        view: View,
        listener: OnItemClickListener<Int>?,
        private val isShareBtnVisible: Boolean,
        private val isFavoriteBtnVisible: Boolean,
    ) : AdapterBase.ViewHolderBase(view, listener) {
        private val hasListener = listener != null
        val quoteTextView: TextView = view.findViewById(R.id.quoteTextView)
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val authorTextView: TextView = view.findViewById(R.id.authorTextView)
        private val actionContainer: LinearLayout = view.findViewById(R.id.actionContainer)
        val shareBtn: MaterialButton = view.findViewById(R.id.shareBtn)
        val favoriteBtn: MaterialButton = view.findViewById(R.id.bookmarkBtn)

        init {
            // Bind listener only if we want to observe
            // on item click events.
            if (listener != null) {
                view.setOnClickListener(this)
                shareBtn.setOnClickListener(this)
                favoriteBtn.setOnClickListener(this)
            }
        }

        fun bind(model: QuoteItem) {
            quoteTextView.text = model.quote(quoteTextView.context)
            titleTextView.text = model.title
            authorTextView.text = model.author

            val isActionable = !model.isPlaceholder && hasListener
            val shareVisible = isShareBtnVisible && isActionable
            val favoriteVisible = isFavoriteBtnVisible && isActionable
            shareBtn.isVisible = shareVisible
            favoriteBtn.isVisible = favoriteVisible
            actionContainer.isVisible = shareVisible || favoriteVisible
            itemView.isClickable = isActionable
            itemView.isFocusable = isActionable

            favoriteBtn.setIconResource(
                if (model.isFavorite) {
                    R.drawable.ic_bookmark
                } else {
                    R.drawable.ic_bookmark_border
                },
            )
            favoriteBtn.contentDescription = favoriteBtn.context.getString(
                if (model.isFavorite) {
                    R.string.bookmark_remove
                } else {
                    R.string.bookmark_add
                },
            )
        }
    }

}
