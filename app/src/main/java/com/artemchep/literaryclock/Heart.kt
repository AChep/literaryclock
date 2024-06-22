package com.artemchep.literaryclock

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.artemchep.config.Config
import com.artemchep.literaryclock.analytics.AnalyticsAbout
import com.artemchep.literaryclock.analytics.AnalyticsDonate
import com.artemchep.literaryclock.analytics.AnalyticsMain
import com.artemchep.literaryclock.analytics.firebase.FirebaseAnalyticsAbout
import com.artemchep.literaryclock.analytics.firebase.FirebaseAnalyticsDonate
import com.artemchep.literaryclock.analytics.firebase.FirebaseAnalyticsMain
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
import com.artemchep.literaryclock.services.WidgetUpdateService
import com.artemchep.literaryclock.widget.LiteraryWidgetUpdater
import com.google.android.material.color.DynamicColors
import com.google.firebase.analytics.FirebaseAnalytics
import io.realm.Realm
import org.acra.ACRA
import org.acra.annotation.AcraCore
import org.acra.annotation.AcraHttpSender
import org.acra.data.StringFormat
import org.acra.sender.HttpSender
import org.kodein.di.*
import org.kodein.di.android.x.androidXModule
import org.kodein.di.bindings.WeakContextScope
import org.solovyev.android.checkout.Billing
import java.text.DateFormat

/**
 * @author Artem Chepurnoy
 */
@AcraCore(
    reportFormat = StringFormat.JSON,
    alsoReportToAndroidFramework = true,
)
@AcraHttpSender(
    uri = BuildConfig.ACRA_URI,
    basicAuthLogin = BuildConfig.ACRA_USERNAME,
    basicAuthPassword = BuildConfig.ACRA_PASSWORD,
    httpMethod = HttpSender.Method.POST,
)
class Heart : Application(), DIAware, Config.OnConfigChangedListener<String> {

    companion object {
        const val UID_WIDGET_UPDATE_JOB = "job::widget_update"
        const val UID_DATABASE_UPDATE_JOB = "job::database_update"

        const val PI_OPEN_MAIN_SCREEN = 1
        const val PI_UPDATE_WIDGET = 2

        private const val ACTION_PREFIX = "com.artemchep.literaryclock"
        const val ACTION_UPDATE_WIDGET = "$ACTION_PREFIX.UPDATE_WIDGET"
        const val ACTION_UPDATE_DATABASE_STATE_CHANGED =
            "$ACTION_PREFIX.UPDATE_DATABASE_STATE_CHANGED"

        const val TAG_LD_TIME = "LiveData<Time>"
        const val TAG_LD_DATABASE_STATE = "LiveData<DatabaseState>"
        const val TAG_LD_MOMENT_ITEM = "LiveData<MomentItem>"
        const val TAG_LD_QUOTE_ITEM = "LiveData<QuoteItem>"
    }

    override val di = DI.lazy {
        import(androidXModule(this@Heart))

        bind<Repo>() with provider { RepoImpl() }

        /*
         * Provides a time live data that actually represents
         * the current time.
         */
        bind<LiveData<Time>>(tag = TAG_LD_TIME) with singleton { TimeLiveData(this@Heart) }

        bind<LiveData<DatabaseState>>(tag = TAG_LD_DATABASE_STATE) with singleton { DatabaseStateLiveData(this@Heart) }

        /*
         * Provides a moment live data that actually represents
         * the moment of a current time.
         */
        bind<LiveData<MomentItem>>(tag = TAG_LD_MOMENT_ITEM) with multiton { time: LiveData<Time> ->
            val repo by di.instance<Repo>()
            val db by di.instance<LiveData<DatabaseState>>(
                tag = TAG_LD_DATABASE_STATE,
            )
            return@multiton MomentLiveData(repo, db, time)
        }

        bind<LiveData<QuoteItem>>(tag = TAG_LD_QUOTE_ITEM) with multiton { moment: LiveData<MomentItem> ->
            return@multiton QuoteLiveData(moment)
        }

        bind<DateFormat>() with provider { android.text.format.DateFormat.getTimeFormat(this@Heart) }

        bind<FirebaseAnalytics>() with scoped(WeakContextScope.of<Context>()).singleton {
            FirebaseAnalytics.getInstance(context)
        }

        bind<AnalyticsMain>() with scoped(WeakContextScope.of<Context>()).singleton {
            val firebaseAnalytics by di.instance<FirebaseAnalytics>()
            FirebaseAnalyticsMain(firebaseAnalytics)
        }

        bind<AnalyticsDonate>() with scoped(WeakContextScope.of<Context>()).singleton {
            val firebaseAnalytics by di.instance<FirebaseAnalytics>()
            FirebaseAnalyticsDonate(firebaseAnalytics)
        }

        bind<AnalyticsAbout>() with scoped(WeakContextScope.of<Context>()).singleton {
            val firebaseAnalytics by di.instance<FirebaseAnalytics>()
            FirebaseAnalyticsAbout(firebaseAnalytics)
        }
    }

    val billing = Billing(this, object : Billing.DefaultConfiguration() {

        override fun getPublicKey(): String {
            return BuildConfig.LICENSE_KEY
        }

    })

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        ACRA.init(this)
    }

    override fun onCreate() {
        super.onCreate()
        // don't schedule anything in crash reporter process
        if (ACRA.isACRASenderServiceProcess())
            return

        DynamicColors.applyToActivitiesIfAvailable(this)

        Realm.init(this)
        Cfg.init(this)
        Cfg.observe(this)
        CfgInternal.init(this)

        // Wait till the app is in the
        // foreground and start the update service.
        ProcessLifecycleOwner.get().lifecycleScope.launchWhenStarted {
            WidgetUpdateService.tryStartOrStop(this@Heart)
        }
    }

    override fun onConfigChanged(keys: Set<String>) {
        if (Cfg.KEY_WIDGET_UPDATE_SERVICE_ENABLED in keys) {
            val context = this@Heart
            LiteraryWidgetUpdater.updateLiteraryWidget(context)

            // Try to restart the update service.
            WidgetUpdateService.tryStartOrStop(this)
        }
    }

}
