package com.artemchep.literaryclock.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Artem Chepurnoy
 */
@Parcelize
data class QuoteItem(
    val quote: String,
    val title: String,
    val asin: String,
    val author: String
) : Parcelable
