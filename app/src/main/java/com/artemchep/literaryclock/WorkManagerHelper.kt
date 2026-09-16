package com.artemchep.literaryclock

import android.content.Context
import androidx.work.*
import com.artemchep.literaryclock.services.DatabaseUpdateWorker
import com.artemchep.literaryclock.services.WidgetUpdateWorker
import java.time.Duration

private const val UID_DATABASE_UPDATE_IMMEDIATE_JOB = "job::database_update_immediate"

fun Context.startUpdateWidgetJob(key: String) {
    val policy = ExistingPeriodicWorkPolicy.UPDATE
    // WorkManager enforces a minimum interval for periodic work. The widget's
    // high-frequency path remains the foreground service; this job is a fallback.
    val duration = Duration.ofMinutes(15L)
    val request = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(duration)
        .setConstraints(
            Constraints.Builder()
                .build()
        )
        .build()

    // Enqueue the periodic work of updating the
    // widget.
    WorkManager.getInstance(this).enqueueUniquePeriodicWork(key, policy, request)
}

fun Context.cancelUpdateWidgetJob(key: String) {
    WorkManager.getInstance(this).cancelUniqueWork(key)
}

fun Context.startUpdateDatabaseJob(key: String) {
    val policy = ExistingPeriodicWorkPolicy.UPDATE
    val duration = Duration.ofDays(20)
    val request = PeriodicWorkRequestBuilder<DatabaseUpdateWorker>(duration)
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(true)
                .build()
        )
        .build()

    // Enqueue the periodic work of updating the
    // database.
    WorkManager.getInstance(this).enqueueUniquePeriodicWork(key, policy, request)
}

fun Context.startUpdateDatabaseImmediateJob() {
    val request = OneTimeWorkRequestBuilder<DatabaseUpdateWorker>()
        .build()

    // Enqueue the immediate work of updating the
    // database.
    WorkManager.getInstance(this).enqueueUniqueWork(
        UID_DATABASE_UPDATE_IMMEDIATE_JOB,
        ExistingWorkPolicy.KEEP,
        request,
    )
}
