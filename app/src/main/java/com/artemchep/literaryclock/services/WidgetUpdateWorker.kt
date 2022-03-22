package com.artemchep.literaryclock.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.artemchep.literaryclock.Cfg
import com.artemchep.literaryclock.utils.ext.ifDebug
import com.artemchep.literaryclock.widget.LiteraryWidgetUpdater

/**
 * @author Artem Chepurnoy
 */
class WidgetUpdateWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    companion object {
        const val TAG = "WidgetUpdateWorker"
    }

    override fun doWork(): Result {
        ifDebug {
            Log.d(TAG, "Updating the widget...")
        }

        // Try to restart the foreground service,
        // if that's possible... fuck these 'awesome' limitations.
        val intent = Intent(applicationContext, WidgetUpdateService::class.java)
        if (Cfg.isWidgetUpdateServiceEnabled) kotlin.runCatching {
            applicationContext.startForegroundService(intent)
        }

        LiteraryWidgetUpdater.updateLiteraryWidget(applicationContext)
        return Result.success()
    }

}
