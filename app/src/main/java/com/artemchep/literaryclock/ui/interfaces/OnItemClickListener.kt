package com.artemchep.literaryclock.ui.interfaces

import android.view.View

/**
 * @author Artem Chepurnoy
 */
interface OnItemClickListener<in T> {

    fun onItemClick(view: View, data: T, position: Int)

}