package com.artemchep.literaryclock.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.artemchep.literaryclock.Batches
import com.artemchep.literaryclock.BuildConfig
import com.artemchep.literaryclock.CfgInternal
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.data.firestore.FirestoreBatchModel
import com.artemchep.literaryclock.data.firestore.FirestoreQuoteModel
import com.artemchep.literaryclock.data.realm.RealmMomentModel
import com.artemchep.literaryclock.utils.sendLocalBroadcastIntent
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QuerySnapshot
import io.realm.Realm
import io.realm.RealmList
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext

class DatabaseUpdateWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        const val TAG = "LiteraryUpdateWorker"

        // True if the database update worker is running at this
        // moment, false otherwise.
        @Volatile
        var isRunning = false
    }

    override fun doWork(): Result {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Updating the literary clock database...")
        }

        setState(true)

        val context = EmptyCoroutineContext +
                CoroutineExceptionHandler { context, throwable ->
                    Log.e(TAG, "The database update went wrong.")
                    throwable.printStackTrace()
                }

        runBlocking(context) {
            val batches = loadNewBatches()

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
                .max()
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
            .get()
    )

    private fun setState(isRunning: Boolean) {
        DatabaseUpdateWorker.isRunning = isRunning

        // Notify that the state has changed.
        val action = Intent(Heart.ACTION_UPDATE_DATABASE_STATE_CHANGED)
        sendLocalBroadcastIntent(applicationContext, action)
    }

}