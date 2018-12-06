package com.artemchep.literaryclock.providers

import android.content.Context
import com.artemchep.literaryclock.models.QuoteItem

/**
 * @author Artem Chepurnoy
 */
interface QuoteProvider {

    fun getQuote(context: Context): QuoteItem

}
