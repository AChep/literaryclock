package com.artemchep.literaryclock.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.artemchep.literaryclock.ui.interfaces.OnItemClickListener

/**
 * @author Artem Chepurnoy
 */
abstract class AdapterBase<M, H : RecyclerView.ViewHolder>(

    /**
     * Mutable list of models to
     * display.
     */
    val models: MutableList<M> = ArrayList()
) : RecyclerView.Adapter<H>(), OnItemClickListener<Int> {

    var onItemClickListener: OnItemClickListener<M>? = null

    operator fun <T> AdapterBase<T, *>.get(position: Int) = models[position]

    override fun getItemCount(): Int = models.size

    override fun onItemClick(view: View, data: Int, position: Int) {
        onItemClickListener?.onItemClick(view, models[position], position)
    }

    fun show(list: List<M>) {
        models.apply {
            clear()
            addAll(list)
        }

        notifyDataSetChanged()
    }

    /**
     * @author Artem Chepurnoy
     */
    open class ViewHolderBase(
        itemView: View,
        private val listener: OnItemClickListener<Int>
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position >= 0) {
                listener.onItemClick(view, position, position)
            }
        }

    }

}
