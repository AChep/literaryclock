package com.artemchep.literaryclock

import android.app.Application
import androidx.lifecycle.LiveData
import com.artemchep.config.Config
import com.artemchep.literaryclock.data.DatabaseState
import com.artemchep.literaryclock.data.Repo
import com.artemchep.literaryclock.data.RepoImpl
import com.artemchep.literaryclock.logic.live.DatabaseStateLiveData
import com.artemchep.literaryclock.logic.live.MomentLiveData
import com.artemchep.literaryclock.logic.live.QuoteLiveData
import com.artemchep.literaryclock.logic.live.TimeLiveData
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.widget.LiteraryWidgetUpdater
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.*
import org.solovyev.android.checkout.Billing
import java.text.DateFormat

/**
 * @author Artem Chepurnoy
 */
class Heart : Application(), KodeinAware, Config.OnConfigChangedListener<String> {

    companion object {
        const val UID_WIDGET_UPDATE_JOB = "job::widget_update"
        const val UID_DATABASE_UPDATE_JOB = "job::database_update"

        const val PI_OPEN_MAIN_SCREEN = 1
        const val PI_UPDATE_WIDGET = 2

        private const val ACTION_PREFIX = "com.artemchep.literaryclock"
        const val ACTION_UPDATE_WIDGET = "$ACTION_PREFIX.UPDATE_WIDGET"
        const val ACTION_UPDATE_DATABASE_STATE_CHANGED =
            "$ACTION_PREFIX.UPDATE_DATABASE_STATE_CHANGED"
    }

    override val kodein = Kodein.lazy {
        bind<Repo>() with provider { RepoImpl() }

        /*
         * Provides a time live data that actually represents
         * the current time.
         */
        bind<LiveData<Time>>() with singleton { TimeLiveData(this@Heart) }

        bind<LiveData<DatabaseState>>() with singleton { DatabaseStateLiveData(this@Heart) }

        /*
         * Provides a moment live data that actually represents
         * the moment of a current time.
         */
        bind<LiveData<MomentItem>>() with multiton { time: LiveData<Time> ->
            val repo by kodein.instance<Repo>()
            val db by kodein.instance<LiveData<DatabaseState>>()
            return@multiton MomentLiveData(repo, db, time)
        }

        bind<LiveData<QuoteItem>>() with multiton { moment: LiveData<MomentItem> ->
            return@multiton QuoteLiveData(moment)
        }

        bind<DateFormat>() with provider { android.text.format.DateFormat.getTimeFormat(this@Heart) }
    }

    val billing = Billing(this, object : Billing.DefaultConfiguration() {

        override fun getPublicKey(): String {
            return BuildConfig.LICENSE_KEY
        }

    })

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Cfg.init(this)
        Cfg.observe(this)
        CfgInternal.init(this)

        // Check the database for updates
        // every day.
        startUpdateDatabaseJob(UID_DATABASE_UPDATE_JOB)
    }

    override fun onConfigChanged(keys: Set<String>) {
        if (Cfg.KEY_WIDGET_TEXT_COLOR in keys ||
            Cfg.KEY_WIDGET_UPDATE_SERVICE_ENABLED in keys
        ) {
            GlobalScope.launch(Dispatchers.Default) {
                val context = this@Heart
                LiteraryWidgetUpdater.updateLiteraryWidget(context)
            }
        }
    }

}
