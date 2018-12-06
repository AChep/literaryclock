package com.artemchep.literaryclock.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.models.DependencyItem
import com.artemchep.literaryclock.ui.interfaces.OnItemClickListener

class DependencyAdapter  : AdapterBase<DependencyItem, DependencyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_dependency, parent, false)
        return ViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = this[position]
        holder.apply {
            nameTextView.text = model.name
            versionTextView.text = model.versionName
        }
    }

    /**
     * @author Artem Chepurnoy
     */
    class ViewHolder(
        view: View,
        listener: OnItemClickListener<Int>
    ) : AdapterBase.ViewHolderBase(view, listener) {

        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val versionTextView: TextView = view.findViewById(R.id.versionTextView)

        init {
            view.setOnClickListener(this)
        }
    }

}