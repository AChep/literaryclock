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

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            postCurrentState()
        }
    }

    override fun onActive() {
        super.onActive()

        val intentFilter = IntentFilter(Heart.ACTION_UPDATE_DATABASE_STATE_CHANGED)
        val lbm = LocalBroadcastManager.getInstance(context)
        lbm.registerReceiver(broadcastReceiver, intentFilter)

        postCurrentState()
    }

    override fun onInactive() {
        val lbm = LocalBroadcastManager.getInstance(context)
        lbm.unregisterReceiver(broadcastReceiver)
        super.onInactive()
    }

    private fun postCurrentState() {
        val state = if (DatabaseUpdateWorker.isRunning) {
            DatabaseState.UPDATING
        } else {
            DatabaseState.IDLE
        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            value = state
        } else {
            mainHandler.post { value = state }
        }
    }

}
