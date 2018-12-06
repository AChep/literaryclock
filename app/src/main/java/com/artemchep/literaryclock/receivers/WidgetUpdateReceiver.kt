package com.artemchep.literaryclock.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.widget.LiteraryWidgetUpdater

/**
 * @author Artem Chepurnoy
 */
class WidgetUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Heart.ACTION_UPDATE_WIDGET -> LiteraryWidgetUpdater.updateLiteraryWidget(context)
        }
    }

}
