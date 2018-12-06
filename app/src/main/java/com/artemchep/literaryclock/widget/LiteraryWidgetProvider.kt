package com.artemchep.literaryclock.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.services.WidgetUpdateWorker
import java.time.Duration

/**
 * @author Artem Chepurnoy
 */
class LiteraryWidgetProvider : AppWidgetProvider() {

    private val period = Duration.ofMinutes(1L)

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        startUpdateWidgetJob()
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        LiteraryWidgetUpdater.updateLiteraryWidget(context)
        startUpdateWidgetJob()
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelUpdateWidgetJob()
    }

    private fun startUpdateWidgetJob() {
        val policy = ExistingPeriodicWorkPolicy.REPLACE
        val request = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(period)
            .setConstraints(
                Constraints.Builder()
                    .build()
            )
            .build()

        // Enqueue the periodic work of updating the
        // widget.
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            Heart.UID_WIDGET_UPDATE_JOB,
            policy,
            request
        )
    }

    private fun cancelUpdateWidgetJob() {
        WorkManager.getInstance().cancelUniqueWork(Heart.UID_WIDGET_UPDATE_JOB)
    }

}