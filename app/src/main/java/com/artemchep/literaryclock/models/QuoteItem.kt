package com.artemchep.literaryclock.models

import android.content.Context
import android.os.Parcelable
import com.artemchep.literaryclock.store.factory.QuoteItemFactory
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
) : Parcelable {
    fun quote(context: Context) = QuoteItemFactory.spanify(context, quote)
}
