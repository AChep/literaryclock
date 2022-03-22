package com.artemchep.literaryclock

import android.content.Context
import androidx.work.*
import com.artemchep.literaryclock.services.DatabaseUpdateWorker
import com.artemchep.literaryclock.services.WidgetUpdateWorker
import java.time.Duration

fun Context.startUpdateWidgetJob(key: String) {
    val policy = ExistingPeriodicWorkPolicy.REPLACE
    val duration = Duration.ofMinutes(1L)
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
    val policy = ExistingPeriodicWorkPolicy.REPLACE
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

    // Enqueue the periodic work of updating the
    // database.
    WorkManager.getInstance(this).enqueue(request)
}
