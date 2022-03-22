package com.artemchep.literaryclock.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.artemchep.literaryclock.widget.LiteraryWidgetUpdater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @author Artem Chepurnoy
 */
class WidgetUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        GlobalScope.launch(Dispatchers.Default) {
            LiteraryWidgetUpdater.updateLiteraryWidget(context)
        }

        goAsync()
    }

}
