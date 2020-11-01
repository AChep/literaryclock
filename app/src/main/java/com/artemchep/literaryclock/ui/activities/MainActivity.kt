package com.artemchep.literaryclock.ui.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import com.artemchep.literaryclock.*
import com.artemchep.literaryclock.logic.viewmodels.ThemeViewModel
import com.artemchep.literaryclock.models.MessageType
import es.dmoral.toasty.Toasty
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI

/**
 * @author Artem Chepurnoy
 */
class MainActivity : AppCompatActivity(), DIAware {

    override val di: DI by closestDI()

    private val themeViewModel: ThemeViewModel by viewModels()

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

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

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

        themeViewModel.setup()
    }

    private fun ThemeViewModel.setup() {
        themeLiveData.observe(this@MainActivity, Observer { theme ->
            delegate.localNightMode = getLocalNightMode(Cfg.appTheme)
        })
    }

}