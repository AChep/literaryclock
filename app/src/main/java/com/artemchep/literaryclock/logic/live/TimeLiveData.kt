package com.artemchep.literaryclock.logic.live

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.utils.currentTime
import java.util.*

/**
 * @author Artem Chepurnoy
 */
class TimeLiveData(private val context: Context) : LiveData<Time>() {

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            postCurrentTime()
        }
    }

    override fun onActive() {
        postCurrentTime()
        super.onActive()

        // Listen to the time changes and post
        // new literature quotes.
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIME_TICK)
        }
        context.registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onInactive() {
        context.unregisterReceiver(broadcastReceiver)
        super.onInactive()
    }

    private fun postCurrentTime() = currentTime.let(::setValue)

}
