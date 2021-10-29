package com.artemchep.literaryclock.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.artemchep.literaryclock.*
import com.artemchep.literaryclock.data.firestore.FirestoreBatchModel
import com.artemchep.literaryclock.data.firestore.FirestoreQuoteModel
import com.artemchep.literaryclock.data.realm.RealmMomentModel
import com.artemchep.literaryclock.models.Message
import com.artemchep.literaryclock.models.MessageType
import com.artemchep.literaryclock.utils.ext.ifDebug
import com.artemchep.literaryclock.utils.sendLocalBroadcastIntent
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import io.realm.Realm
import io.realm.RealmList
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext

class DatabaseUpdateWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

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
            val batches = loadNewBatches()
            ifDebug {
                Log.d(TAG, "${batches}")
            }

            // Load all new quotes and add them to
            // database.
            batches
                .map {
                    val quotesRef = Batches.one(it.id).quotes().ref
                    async {
                        val task = quotesRef.get()
                        Tasks.await(task)
                    }
                }
                .map { it.await() }
                .flatten()
                .map {
                    it.toObject(FirestoreQuoteModel::class.java)
                        .apply {
                            key = it.id
                        }
                }
                .groupBy { it.time }
                .map {
                    // Map it to realm model
                    RealmMomentModel().apply {
                        key = it.key
                        quotes = it.value.mapTo(RealmList(), FirestoreQuoteModel::toRealmModel)
                    }
                }
                .also { entries ->
                    // Insert new quotes to our
                    // database.
                    Realm
                        .getDefaultInstance()
                        .executeTransaction { it.insertOrUpdate(entries) }
                }

            // Get new synchronization time to
            // remember.
            batches.documents
                .mapNotNull { it.toObject(FirestoreBatchModel::class.java) }
                .map { it.timestamp?.toDate()?.time ?: 0L }
                .maxOrNull()
                // Save new time to local
                // storage
                ?.let { new ->
                    CfgInternal.edit(applicationContext) {
                        CfgInternal.syncTimestamp = new
                    }
                }

        }

        setState(false)

        return Result.success()
    }

    private fun loadNewBatches(): QuerySnapshot = Tasks.await(
        Batches.ref
            .whereGreaterThan(
                FirestoreBatchModel::timestamp.name,
                Timestamp(Date(CfgInternal.syncTimestamp))
            )
            .get(Source.SERVER)
    )

    private fun setState(isRunning: Boolean) {
        DatabaseUpdateWorker.isRunning = isRunning

        // Notify that the state has changed.
        val action = Intent(Heart.ACTION_UPDATE_DATABASE_STATE_CHANGED)
        sendLocalBroadcastIntent(applicationContext, action)
    }

}