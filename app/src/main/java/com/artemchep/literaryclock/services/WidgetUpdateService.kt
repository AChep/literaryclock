package com.artemchep.literaryclock.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.artemchep.config.Config
import com.artemchep.literaryclock.*
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.utils.ext.ifDebug
import com.artemchep.literaryclock.widget.LiteraryWidgetUpdater
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author Artem Chepurnoy
 */
class WidgetUpdateService : Service(), KodeinAware, Config.OnConfigChangedListener<String> {

    companion object {
        const val TAG = "WidgetUpdateService"
    }

    override lateinit var kodein: Kodein

    /**
     * Executor service to update
     * the widget.
     */
    private lateinit var executor: ExecutorService

    private val timeLiveData by instance<LiveData<Time>>()

    private val timeObserver = Observer<Time> {
        val powerManager = getSystemService<PowerManager>()!!
        if (powerManager.isInteractive) executor.execute {
            ifDebug {
                Log.d(TAG, "Updating the widget...")
            }

            LiteraryWidgetUpdater.updateLiteraryWidget(this)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val notification = createNotification()
        val notificationChannel = createNotificationChannel()

        // Create notification channel
        val nm = getSystemService<NotificationManager>()!!
        nm.createNotificationChannel(notificationChannel)

        // Start service foreground and post the notification
        startForeground(NOTIFICATION_UID_WIDGET_UPDATE_SERVICE, notification)

        kodein = (applicationContext as Heart).kodein
        executor = Executors.newSingleThreadExecutor()

        // Observe the current time
        timeLiveData.observeForever(timeObserver)

        // Observe the config, so we can shutdown ourselves
        // if user wants to.
        Cfg.observe(this)
        checkWidgetUpdateServiceEnabledOption()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    private fun createNotificationChannel(): NotificationChannel {
        return NotificationChannel(
            NOTIFICATION_CHANNEL_WIDGET_UPDATE_SERVICE,
            getString(R.string.notification_widget_update_service_channel),
            NotificationManager.IMPORTANCE_MIN
        )
    }

    private fun createNotification(): Notification {
        val title = getString(R.string.notification_widget_update_service_title)
        val text = getString(R.string.notification_widget_update_service_text)
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_WIDGET_UPDATE_SERVICE)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_time)
            .setColor(0xFFf4ff81.toInt())
            .setShowWhen(false)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        Cfg.removeObserver(this)
        timeLiveData.removeObserver(timeObserver)
        executor.shutdown()
    }

    override fun onConfigChanged(keys: Set<String>) {
        if (Cfg.KEY_WIDGET_UPDATE_SERVICE_ENABLED in keys) {
            // Check if the update service is allowed by
            // user to run, otherwise kill it.
            checkWidgetUpdateServiceEnabledOption()
        }
    }

    private fun checkWidgetUpdateServiceEnabledOption() {
        if (Cfg.isWidgetUpdateServiceEnabled.not()) {
            stopSelf()
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null

}

