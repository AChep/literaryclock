package com.artemchep.literaryclock.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.artemchep.literaryclock.*
import com.artemchep.literaryclock.data.room.DatabaseImporter
import com.artemchep.literaryclock.data.room.LegacyRealmCleaner
import com.artemchep.literaryclock.models.Message
import com.artemchep.literaryclock.models.MessageType
import com.artemchep.literaryclock.utils.ext.ifDebug
import com.artemchep.literaryclock.utils.sendLocalBroadcastIntent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kodein.di.direct
import org.kodein.di.instance
import java.util.concurrent.atomic.AtomicInteger


class DatabaseUpdateWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        const val TAG = "LiteraryUpdateWorker"

        // True if the database update worker is running at this
        // moment, false otherwise.
        val isRunning: Boolean
            get() = runningWorkerCount.get() > 0

        private val runningWorkerCount = AtomicInteger(0)
    }

    override suspend fun doWork(): Result {
        ifDebug {
            Log.d(TAG, "Updating the literary clock database...")
        }

        setState(true)

        return try {
            val importer = (applicationContext as Heart).di.direct.instance<DatabaseImporter>()
            val jsonString = loadData()
            importer.importJson(jsonString)
            LegacyRealmCleaner.deleteDefaultRealmFiles(applicationContext)
            Result.success()
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                throw throwable
            }

            Log.e(TAG, "The database update went wrong.", throwable)

            val message = Message(
                type = MessageType.ERROR,
                text = {
                    getString(R.string.error_sync_failed)
                }
            )
            messageLiveEvent.postValue(message)

            Result.failure()
        } finally {
            setState(false)
        }
    }

    private suspend fun loadData(): String = withContext(Dispatchers.IO) {
            val inputStream = applicationContext.resources.openRawResource(R.raw.database)
            inputStream.reader().use { reader -> reader.readText() }
    }

    private fun setState(isRunning: Boolean) {
        if (isRunning) {
            runningWorkerCount.incrementAndGet()
        } else {
            runningWorkerCount.updateAndGet { count ->
                (count - 1).coerceAtLeast(0)
            }
        }

        // Notify that the state has changed.
        val action = Intent(Heart.ACTION_UPDATE_DATABASE_STATE_CHANGED)
        sendLocalBroadcastIntent(applicationContext, action)
    }

}
