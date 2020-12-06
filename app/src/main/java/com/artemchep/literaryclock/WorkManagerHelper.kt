package com.artemchep.literaryclock

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
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

fun Context.startUpdateDatabaseJob(key: String) {
    val policy = ExistingPeriodicWorkPolicy.REPLACE
    val duration = Duration.ofDays(5)
    val request = PeriodicWorkRequestBuilder<DatabaseUpdateWorker>(duration)
        .setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(true)
                .build()
        )
        .build()

    // Enqueue the periodic work of updating the
    // database.
    WorkManager.getInstance(this).enqueueUniquePeriodicWork(key, policy, request)
}
