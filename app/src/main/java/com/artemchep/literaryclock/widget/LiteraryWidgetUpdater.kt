package com.artemchep.literaryclock.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import com.artemchep.literaryclock.Cfg
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.data.Repo
import com.artemchep.literaryclock.data.room.LiteraryClockDao
import com.artemchep.literaryclock.data.room.QuoteEntity
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.receivers.WidgetUpdateReceiver
import com.artemchep.literaryclock.store.factory.QuoteItemFactory
import com.artemchep.literaryclock.ui.activities.MainActivity
import com.artemchep.literaryclock.utils.currentTime
import org.kodein.di.android.closestDI
import org.kodein.di.instance

/**
 * @author Artem Chepurnoy
 */
object LiteraryWidgetUpdater {

    /**
     * Defines how many moments the updater retrieves at
     * once from the database.
     */
    const val RANGE_SIZE = 60 // 60 minutes

    /**
     * Current range of [quotes] that we
     * hold.
     */
    @Volatile
    private var range: ClosedRange<Time> = Time(-1).let { it..it }

    @Volatile
    private lateinit var quotes: List<QuoteItem>

    @UiThread
    @WorkerThread
    suspend fun updateLiteraryWidget(context: Context) {
        val currentTime = currentTime
        val rawQuote = getCachedQuote(currentTime) ?: run {
            val kodein by closestDI(context)
            val repo by kodein.instance<Repo>()
            val requestedRange = currentTime..Time(currentTime.time + RANGE_SIZE)
            val loadedQuotes = repo
                .getMoments(requestedRange)
                .map { it.quotes.random() }
            synchronized(this) {
                getCachedQuoteLocked(currentTime) ?: run {
                    range = requestedRange
                    quotes = loadedQuotes
                    val offset = currentTime.time - requestedRange.start.time
                    loadedQuotes[offset]
                }
            }
        }
        val quote = hydrateFavoriteState(context, rawQuote)

        try {
            updateLiteraryWidget(context, quote)
        } catch (e: Exception) {
            // Do nothing
        }
    }

    private fun getCachedQuote(currentTime: Time): QuoteItem? = synchronized(this) {
        getCachedQuoteLocked(currentTime)
    }

    fun getCachedQuote(quoteKey: String): QuoteItem? = synchronized(this) {
        if (::quotes.isInitialized.not()) {
            return@synchronized null
        }

        quotes.firstOrNull { it.key == quoteKey }
    }

    suspend fun resolveQuoteForWidget(
        context: Context,
        quoteKey: String,
        isFavorite: Boolean,
    ): QuoteItem? {
        if (quoteKey.isBlank()) {
            return null
        }

        return getCachedQuote(quoteKey)?.copy(isFavorite = isFavorite)
            ?: getQuoteByKey(context, quoteKey)?.let { quote ->
                QuoteItemFactory.transform(
                    origin = quote,
                    isFavorite = isFavorite,
                )
            }
    }

    private fun getCachedQuoteLocked(currentTime: Time): QuoteItem? {
        if (currentTime !in range || ::quotes.isInitialized.not()) {
            return null
        }

        val offset = currentTime.time - range.start.time
        return quotes[offset]
    }

    fun updateLiteraryWidget(context: Context, quote: QuoteItem) {
        val remoteViews = createLiteraryRemoteViews(context, quote)
        val componentName = ComponentName(context, LiteraryWidgetProvider::class.java)
        AppWidgetManager
            .getInstance(context)
            .updateAppWidget(componentName, remoteViews)
    }

    private suspend fun hydrateFavoriteState(
        context: Context,
        quote: QuoteItem,
    ): QuoteItem {
        if (quote.isPlaceholder || quote.key.isBlank()) {
            return quote.copy(isFavorite = false)
        }

        val kodein by closestDI(context)
        val dao by kodein.instance<LiteraryClockDao>()
        val isFavorite = dao.isFavoriteQuote(quote.key)
        return quote.copy(isFavorite = isFavorite)
    }

    private suspend fun getQuoteByKey(
        context: Context,
        quoteKey: String,
    ): QuoteEntity? {
        val kodein by closestDI(context)
        val dao by kodein.instance<LiteraryClockDao>()
        return dao.getQuoteByKey(quoteKey)
    }

    private fun createLiteraryRemoteViews(context: Context, quote: QuoteItem) = RemoteViews(
        context.packageName,
        R.layout.widget
    ).apply {
        setTextViewText(R.id.quoteTextView, quote.quote(context))
        setTextViewText(R.id.titleTextView, quote.title)
        setTextViewText(R.id.authorTextView, quote.author)
        bindFavoriteButton(context, quote)

        if (Cfg.isWidgetUpdateServiceEnabled) {
            setViewVisibility(R.id.refreshBtn, View.GONE)
        } else {
            val intent = Intent(context, WidgetUpdateReceiver::class.java).apply {
                action = Heart.ACTION_UPDATE_WIDGET
                addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                Heart.PI_UPDATE_WIDGET,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            setViewVisibility(R.id.refreshBtn, View.VISIBLE)
            setOnClickPendingIntent(R.id.refreshBtn, pendingIntent)
        }

        // On click open the main screen.
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            Heart.PI_OPEN_MAIN_SCREEN,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        setOnClickPendingIntent(R.id.quoteTextView, pendingIntent)
    }

    private fun RemoteViews.bindFavoriteButton(
        context: Context,
        quote: QuoteItem,
    ) {
        if (quote.isPlaceholder || quote.key.isBlank()) {
            setViewVisibility(R.id.favoriteBtn, View.GONE)
            return
        }

        val intent = Intent(context, WidgetUpdateReceiver::class.java).apply {
            action = Heart.ACTION_TOGGLE_WIDGET_FAVORITE
            addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            data = favoriteIntentData(quote.key)
            putExtra(WidgetUpdateReceiver.EXTRA_QUOTE_KEY, quote.key)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            Heart.PI_TOGGLE_WIDGET_FAVORITE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val icon = if (quote.isFavorite) {
            R.drawable.ic_bookmark
        } else {
            R.drawable.ic_bookmark_border
        }
        val contentDescription = context.getString(
            if (quote.isFavorite) {
                R.string.bookmark_remove
            } else {
                R.string.bookmark_add
            },
        )

        setViewVisibility(R.id.favoriteBtn, View.VISIBLE)
        setImageViewResource(R.id.favoriteBtn, icon)
        setContentDescription(R.id.favoriteBtn, contentDescription)
        setOnClickPendingIntent(R.id.favoriteBtn, pendingIntent)
    }

    private fun favoriteIntentData(quoteKey: String): Uri = Uri.parse(
        "literaryclock://widget/favorite/$quoteKey",
    )

}
