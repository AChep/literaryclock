package com.artemchep.literaryclock.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.artemchep.literaryclock.services.WidgetUpdateService

/**
 * @author Artem Chepurnoy
 */
class LiteraryWidgetProvider : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        WidgetUpdateService.tryStartOrStop(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        LiteraryWidgetUpdater.updateLiteraryWidget(context)
        WidgetUpdateService.tryStartOrStop(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WidgetUpdateService.tryStartOrStop(context)
    }

}