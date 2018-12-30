package com.artemchep.literaryclock.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import androidx.work.WorkManager
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.startUpdateWidgetJob

/**
 * @author Artem Chepurnoy
 */
class LiteraryWidgetProvider : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        startUpdateWidgetJob(Heart.UID_WIDGET_UPDATE_JOB)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        LiteraryWidgetUpdater.updateLiteraryWidget(context)
        startUpdateWidgetJob(Heart.UID_WIDGET_UPDATE_JOB)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelUpdateWidgetJob()
    }

    private fun cancelUpdateWidgetJob() {
        WorkManager.getInstance().cancelUniqueWork(Heart.UID_WIDGET_UPDATE_JOB)
    }

}