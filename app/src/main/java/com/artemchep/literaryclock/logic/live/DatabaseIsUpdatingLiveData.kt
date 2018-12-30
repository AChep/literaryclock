package com.artemchep.literaryclock.logic.live

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.services.DatabaseUpdateWorker

/**
 * @author Artem Chepurnoy
 */
class DatabaseIsUpdatingLiveData(private val context: Context) : LiveData<Boolean>() {

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            postCurrentState()
        }
    }

    override fun onActive() {
        postCurrentState()
        super.onActive()

        val intentFilter = IntentFilter(Heart.ACTION_UPDATE_DATABASE_STATE_CHANGED)
        val lbm = LocalBroadcastManager.getInstance(context)
        lbm.registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onInactive() {
        val lbm = LocalBroadcastManager.getInstance(context)
        lbm.unregisterReceiver(broadcastReceiver)
        super.onInactive()
    }

    private fun postCurrentState(): Unit = postValue(DatabaseUpdateWorker.isRunning)

}
