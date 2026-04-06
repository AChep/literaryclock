package com.artemchep.literaryclock.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.analytics.AnalyticsMain
import com.artemchep.literaryclock.data.room.FavoriteQuoteEntity
import com.artemchep.literaryclock.data.room.LiteraryClockDao
import com.artemchep.literaryclock.widget.LiteraryWidgetUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import org.kodein.di.on

/**
 * @author Artem Chepurnoy
 */
class WidgetUpdateReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_QUOTE_KEY = "quote_key"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                when (intent.action) {
                    Heart.ACTION_TOGGLE_WIDGET_FAVORITE -> toggleFavorite(context, intent)
                    else -> LiteraryWidgetUpdater.updateLiteraryWidget(context)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun toggleFavorite(context: Context, intent: Intent) {
        val quoteKey = intent.getStringExtra(EXTRA_QUOTE_KEY).orEmpty()
        if (quoteKey.isBlank()) {
            LiteraryWidgetUpdater.updateLiteraryWidget(context)
            return
        }

        val kodein by closestDI(context)
        val dao by kodein.instance<LiteraryClockDao>()
        val analytics by kodein.on(context).instance<AnalyticsMain>()

        val isFavorite = dao.isFavoriteQuote(quoteKey)
        if (isFavorite) {
            dao.deleteFavoriteByQuoteKey(quoteKey)
        } else {
            dao.upsertFavorite(
                FavoriteQuoteEntity(
                    quoteKey = quoteKey,
                    favoritedAt = System.currentTimeMillis(),
                ),
            )
        }

        val updatedQuote = LiteraryWidgetUpdater.resolveQuoteForWidget(
            context = context,
            quoteKey = quoteKey,
            isFavorite = !isFavorite,
        )
        if (isFavorite) {
            updatedQuote?.let(analytics::logQuoteFavoriteRemove)
        } else {
            updatedQuote?.let(analytics::logQuoteFavoriteAdd)
        }

        if (updatedQuote != null) {
            LiteraryWidgetUpdater.updateLiteraryWidget(context, updatedQuote)
        } else {
            LiteraryWidgetUpdater.updateLiteraryWidget(context)
        }
    }

}
