package com.artemchep.literaryclock.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.artemchep.literaryclock.services.WidgetUpdateService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                LiteraryWidgetUpdater.updateLiteraryWidget(context)
            } finally {
                pendingResult.finish()
            }
        }
        WidgetUpdateService.tryStartOrStop(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WidgetUpdateService.tryStartOrStop(context)
    }

}
