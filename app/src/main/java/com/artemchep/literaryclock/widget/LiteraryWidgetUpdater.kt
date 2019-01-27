package com.artemchep.literaryclock.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import com.artemchep.literaryclock.Cfg
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.data.Repo
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.receivers.WidgetUpdateReceiver
import com.artemchep.literaryclock.ui.activities.MainActivity
import com.artemchep.literaryclock.utils.currentTime
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

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
    fun updateLiteraryWidget(context: Context) {
        val currentTime = currentTime
        val quote = synchronized(this) {
            if (currentTime in range) {
                // No need to update the data,
                // everything is good.
                quotes
            } else {
                val kodein by closestKodein(context)
                val repo by kodein.instance<Repo>()

                range = currentTime..Time(currentTime.time + RANGE_SIZE)
                repo
                    .getMoments(range)
                    .map { it.quotes.random() }
                    .also {
                        // Remember the list of quotes that we
                        // retrieved.
                        quotes = it
                    }
            }.let { quotes ->
                val offset = currentTime.time - range.start.time
                return@let quotes[offset]
            }
        }

        updateLiteraryWidget(context, quote)
    }

    private fun updateLiteraryWidget(context: Context, quote: QuoteItem) {
        val remoteViews = createLiteraryRemoteViews(context, quote)
        val componentName = ComponentName(context, LiteraryWidgetProvider::class.java)
        AppWidgetManager
            .getInstance(context)
            .updateAppWidget(componentName, remoteViews)
    }

    private fun createLiteraryRemoteViews(context: Context, quote: QuoteItem) = RemoteViews(
        context.packageName,
        R.layout.widget
    ).apply {
        setTextViewText(R.id.quoteTextView, quote.quote)
        setTextViewText(R.id.titleTextView, quote.title)
        setTextViewText(R.id.authorTextView, quote.author)

        val colorPrimary = Cfg.widgetTextColor
        setTextColor(R.id.quoteTextView, colorPrimary)
        setTextColor(R.id.titleTextView, colorPrimary)
        setTextColor(R.id.authorTextView, colorPrimary)

        if (Cfg.isWidgetUpdateServiceEnabled) {
            //
        } else {
            val intent = Intent(context, WidgetUpdateReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                Heart.PI_UPDATE_WIDGET,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
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
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        setOnClickPendingIntent(R.id.quoteTextView, pendingIntent)
    }

}
