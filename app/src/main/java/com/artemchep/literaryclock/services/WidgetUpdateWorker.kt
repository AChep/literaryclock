package com.artemchep.literaryclock.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.artemchep.literaryclock.BuildConfig
import com.artemchep.literaryclock.widget.LiteraryWidgetUpdater

/**
 * @author Artem Chepurnoy
 */
class WidgetUpdateWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        const val TAG = "WidgetUpdateWorker"
    }

    override fun doWork(): Result {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Updating the widget...")
        }

        try {
            val intent = Intent(applicationContext, WidgetUpdateService::class.java)
            applicationContext.startForegroundService(intent)
        } catch (e: Exception) {
        }

        LiteraryWidgetUpdater.updateLiteraryWidget(applicationContext)
        return Result.SUCCESS
    }

}
