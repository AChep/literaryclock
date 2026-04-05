package com.artemchep.literaryclock.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.artemchep.literaryclock.widget.LiteraryWidgetUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Artem Chepurnoy
 */
class WidgetUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                LiteraryWidgetUpdater.updateLiteraryWidget(context)
            } finally {
                pendingResult.finish()
            }
        }
    }

}
