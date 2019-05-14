package com.artemchep.literaryclock.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import com.artemchep.config.Config
import com.artemchep.literaryclock.*
import com.artemchep.literaryclock.models.MessageType
import es.dmoral.toasty.Toasty

/**
 * @author Artem Chepurnoy
 */
class MainActivity : AppCompatActivity() {

    private val cfgObserver = object : Config.OnConfigChangedListener<String> {
        override fun onConfigChanged(keys: Set<String>) {
            if (Cfg.KEY_APP_THEME in keys) {
                delegate.localNightMode = getLocalNightModeFromCfg()
            }
        }
    }

    override fun getDelegate(): AppCompatDelegate =
        super.getDelegate().apply {
            localNightMode = getLocalNightModeFromCfg()
        }

    private fun getLocalNightModeFromCfg() =
        when (Cfg.appTheme) {
            Cfg.APP_THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            Cfg.APP_THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Show all the messages as
        // toasts.
        messageLiveEvent.observe(this, Observer {
            val context = this@MainActivity
            val text = it.text.invoke(context)
            when (it.type) {
                MessageType.SUCCESS -> Toasty.success(context, text)
                MessageType.NORMAL -> Toasty.normal(context, text)
                MessageType.ERROR -> Toasty.error(context, text)
            }
        })

        // Check the database for updates
        // every day.
        startUpdateDatabaseJob(Heart.UID_DATABASE_UPDATE_JOB)

        Cfg.observe(cfgObserver)
    }

    override fun onDestroy() {
        Cfg.removeObserver(cfgObserver)
        super.onDestroy()
    }

}