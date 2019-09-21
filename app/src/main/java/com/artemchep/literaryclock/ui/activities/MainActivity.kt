package com.artemchep.literaryclock.ui.activities

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import com.artemchep.literaryclock.*
import com.artemchep.literaryclock.logic.viewmodels.ThemeViewModel
import com.artemchep.literaryclock.models.MessageType
import es.dmoral.toasty.Toasty
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinContext
import org.kodein.di.android.kodein
import org.kodein.di.generic.kcontext

/**
 * @author Artem Chepurnoy
 */
class MainActivity : AppCompatActivity(), KodeinAware {

    override val kodein: Kodein by kodein()

    override val kodeinContext: KodeinContext<Context> = kcontext(this)

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