package com.artemchep.literaryclock.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.artemchep.literaryclock.*
import com.artemchep.literaryclock.data.firestore.FirestoreQuoteModel
import com.artemchep.literaryclock.data.realm.RealmMomentModel
import com.artemchep.literaryclock.models.Message
import com.artemchep.literaryclock.models.MessageType
import com.artemchep.literaryclock.utils.ext.ifDebug
import com.artemchep.literaryclock.utils.sendLocalBroadcastIntent
import io.realm.Realm
import io.realm.RealmList
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import kotlin.coroutines.EmptyCoroutineContext


class DatabaseUpdateWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        const val TAG = "LiteraryUpdateWorker"

        // True if the database update worker is running at this
        // moment, false otherwise.
        @Volatile
        var isRunning = false
    }

    override suspend fun doWork(): Result {
        ifDebug {
            Log.d(TAG, "Updating the literary clock database...")
        }

        setState(true)

        val context = EmptyCoroutineContext +
                CoroutineExceptionHandler { _, throwable ->
                    Log.e(TAG, "The database update went wrong.")
                    throwable.printStackTrace()

                    val message = Message(
                        type = MessageType.ERROR,
                        text = {
                            getString(R.string.error_sync_failed)
                        }
                    )
                    messageLiveEvent.postValue(message)
                }

        withContext(context) {
            val data = loadData()
            data
                .groupBy { it.time }
                .map {
                    // Map it to realm model
                    RealmMomentModel().apply {
                        key = it.key
                        quotes = it.value.mapTo(RealmList(), FirestoreQuoteModel::toRealmModel)
                    }
                }
                .toList()
                .also { entries ->
                    // Insert new quotes to our
                    // database.
                    Realm
                        .getDefaultInstance()
                        .executeTransaction { it.insertOrUpdate(entries) }
                }
        }

        setState(false)

        return Result.success()
    }

    private suspend fun loadData(): List<FirestoreQuoteModel> = withContext(Dispatchers.Default) {
        val jsonString = withContext(Dispatchers.IO) {
            val inputStream = applicationContext.resources.openRawResource(R.raw.database)
            inputStream.reader().use { reader -> reader.readText() }
        }
        val jsonArray = JSONArray(jsonString)
        // Parse JSON to a format previously used by
        // the Firebase database.
        (0 until jsonArray.length())
            .map { i ->
                val obj = jsonArray.getJSONObject(i)
                FirestoreQuoteModel(
                    key = obj.getString("key"),
                    quote = obj.getString("quote"),
                    title = obj.optString("title"),
                    author = obj.optString("author"),
                    asin = obj.optString("asin"),
                    time = obj.getInt("time"),
                )
            }
    }

    private fun setState(isRunning: Boolean) {
        DatabaseUpdateWorker.isRunning = isRunning

        // Notify that the state has changed.
        val action = Intent(Heart.ACTION_UPDATE_DATABASE_STATE_CHANGED)
        sendLocalBroadcastIntent(applicationContext, action)
    }

}