package com.artemchep.literaryclock.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
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
import com.artemchep.literaryclock.widget.LiteraryWidgetProvider
import com.artemchep.literaryclock.widget.LiteraryWidgetUpdater
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author Artem Chepurnoy
 */
class WidgetUpdateService : Service(), DIAware, Config.OnConfigChangedListener<String> {

    companion object {
        const val TAG = "WidgetUpdateService"

        fun tryStartOrStop(context: Context) {
            val hasActiveWidget = kotlin.run {
                val componentName = ComponentName(context, LiteraryWidgetProvider::class.java)
                val appIds = AppWidgetManager
                    .getInstance(context)
                    .getAppWidgetIds(componentName)
                appIds?.any { it != AppWidgetManager.INVALID_APPWIDGET_ID } == true
            }

            val intent = Intent(context, WidgetUpdateService::class.java)
            if (hasActiveWidget) {
                if (Cfg.isWidgetUpdateServiceEnabled) {
                    // WHEN YOU ADD A WIDGET, THE APP IS NOT FOREGROUND!
                    val e = kotlin.runCatching {
                        context.startForegroundService(intent)
                    }.exceptionOrNull()
                    if (e != null) context.startUpdateWidgetJob(Heart.UID_WIDGET_UPDATE_JOB)
                } else {
                    context.startUpdateWidgetJob(Heart.UID_WIDGET_UPDATE_JOB)
                }
            } else {
                context.cancelUpdateWidgetJob(Heart.UID_WIDGET_UPDATE_JOB)
                // Try to stop currently running update service,
                // if there is any.
                kotlin.runCatching {
                    context.stopService(intent)
                }
            }
        }
    }

    override lateinit var di: DI

    /**
     * Executor service to update
     * the widget.
     */
    private lateinit var executor: ExecutorService

    private val timeLiveData by instance<LiveData<Time>>(
        tag = Heart.TAG_LD_TIME,
    )

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
        startForeground()

        di = (applicationContext as Heart).di
        executor = Executors.newSingleThreadExecutor()

        // Observe the current time
        timeLiveData.observeForever(timeObserver)

        // Observe the config, so we can shutdown ourselves
        // if user wants to.
        Cfg.observe(this)
        checkWidgetUpdateServiceEnabledOption()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        return Service.START_STICKY
    }

    private fun startForeground() {
        val notification = createNotification()
        val notificationChannel = createNotificationChannel()

        // Create notification channel
        val nm = getSystemService<NotificationManager>()!!
        nm.createNotificationChannel(notificationChannel)

        // Start service foreground and post the notification
        startForeground(NOTIFICATION_UID_WIDGET_UPDATE_SERVICE, notification)
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

