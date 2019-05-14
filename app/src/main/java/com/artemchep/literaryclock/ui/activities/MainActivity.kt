package com.artemchep.literaryclock.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.artemchep.literaryclock.*
import com.artemchep.literaryclock.logic.viewmodels.ThemeViewModel
import com.artemchep.literaryclock.models.MessageType
import es.dmoral.toasty.Toasty

/**
 * @author Artem Chepurnoy
 */
class MainActivity : AppCompatActivity() {

    private lateinit var themeViewModel: ThemeViewModel

    override fun getDelegate(): AppCompatDelegate =
        super.getDelegate().apply {
            localNightMode = getLocalNightMode(Cfg.appTheme)
        }

    private fun getLocalNightMode(theme: String) =
        when (theme) {
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

        themeViewModel = ViewModelProviders.of(this).get(ThemeViewModel::class.java)
        themeViewModel.setup()
    }

    private fun ThemeViewModel.setup() {
        themeLiveData.observe(this@MainActivity, Observer { theme ->
            delegate.localNightMode = getLocalNightMode(Cfg.appTheme)
        })
    }

}