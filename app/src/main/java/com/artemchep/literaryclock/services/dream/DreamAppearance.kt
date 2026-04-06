package com.artemchep.literaryclock.services.dream

import android.content.Context
import androidx.core.content.res.use
import com.artemchep.literaryclock.Cfg
import com.artemchep.literaryclock.store.factory.QuoteItemFactory
import java.util.Locale

object DreamAppearance {
    fun resolveDefaultTextColor(context: Context): Int =
        context.obtainStyledAttributes(
            intArrayOf(android.R.attr.textColorPrimary),
        ).use { typedArray ->
            typedArray.getColor(0, QuoteItemFactory.DEFAULT_PRIMARY_COLOR)
        }

    fun resolveEffectiveTextColor(context: Context): Int =
        Cfg.dreamTextColor ?: resolveDefaultTextColor(context)

    fun resolveEffectiveAccentColor(): Int =
        Cfg.dreamAccentColor ?: QuoteItemFactory.DEFAULT_PRIMARY_COLOR

    fun formatColor(color: Int): String = String.format(Locale.US, "#%08X", color)
}
