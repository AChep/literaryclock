package com.artemchep.literaryclock.store.factory

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.text.buildSpannedString
import com.artemchep.literaryclock.data.room.QuoteEntity
import com.artemchep.literaryclock.models.QuoteItem


/**
 * @author Artem Chepurnoy
 */
object QuoteItemFactory {

    private const val BEGIN = "<strong>"
    private const val END = "</strong>"

    fun spanify(
        context: Context,
        quote: String,
    ) = buildSpannedString {
        val primaryColor = 0xFF5891ee
        quote
            .splitToSequence(BEGIN)
            .forEachIndexed { index, s ->
                if (index == 0) {
                    append(s)
                    return@forEachIndexed
                }

                s.splitToSequence(END)
                    .forEachIndexed { j, s ->
                        append(s)
                        if (j == 0) {
                            setSpan(
                                StyleSpan(Typeface.BOLD),
                                length - s.length,
                                length,
                                Spannable.SPAN_INCLUSIVE_EXCLUSIVE,
                            )
                            setSpan(
                                ForegroundColorSpan(primaryColor.toInt()),
                                length - s.length,
                                length,
                                Spannable.SPAN_INCLUSIVE_EXCLUSIVE,
                            )
                        }
                    }
            }
    }

    fun transform(origin: QuoteEntity, isFavorite: Boolean = false): QuoteItem = transform(
        key = origin.key,
        quote = origin.quote,
        title = origin.title,
        asin = origin.asin,
        author = origin.author,
        isFavorite = isFavorite,
    )

    fun transform(
        key: String,
        quote: String,
        title: String,
        asin: String,
        author: String,
        isFavorite: Boolean = false,
        isPlaceholder: Boolean = false,
    ): QuoteItem = QuoteItem(
        key = key,
        quote = quote,
        title = title,
        asin = asin,
        author = author,
        isFavorite = isFavorite,
        isPlaceholder = isPlaceholder,
    )

}
