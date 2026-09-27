package com.artemchep.literaryclock.logic.live

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.data.DatabaseState
import com.artemchep.literaryclock.services.DatabaseUpdateWorker

/**
 * @author Artem Chepurnoy
 */
class DatabaseStateLiveData(private val context: Context) : LiveData<DatabaseState>() {

    private val mainHandler = Handler(Looper.getMainLooper())

    private var broadcastReceiver: BroadcastReceiver? = null

    override fun onActive() {
        super.onActive()

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                postCurrentState(this)
            }
        }
        broadcastReceiver = receiver
        val intentFilter = IntentFilter(Heart.ACTION_UPDATE_DATABASE_STATE_CHANGED)
        val lbm = LocalBroadcastManager.getInstance(context)
        lbm.registerReceiver(receiver, intentFilter)

        postCurrentState(receiver)
    }

    override fun onInactive() {
        val lbm = LocalBroadcastManager.getInstance(context)
        broadcastReceiver?.let(lbm::unregisterReceiver)
        broadcastReceiver = null
        super.onInactive()
    }

    private fun postCurrentState(receiver: BroadcastReceiver) {
        val state = if (DatabaseUpdateWorker.isRunning) {
            DatabaseState.UPDATING
        } else {
            DatabaseState.IDLE
        }

        val update = Runnable {
            // A new activation publishes a fresh snapshot. Ignore callbacks
            // queued by a receiver from an earlier activation.
            if (broadcastReceiver === receiver) {
                value = state
            }
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            update.run()
        } else {
            mainHandler.post(update)
        }
    }

}
