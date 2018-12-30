package com.artemchep.literaryclock.utils

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

fun sendLocalBroadcastIntent(context: Context, intent: Intent) {
    val lbm = LocalBroadcastManager.getInstance(context)
    lbm.sendBroadcast(intent)
}
