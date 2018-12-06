package com.artemchep.literaryclock

import android.app.Application
import android.util.Log
import androidx.core.util.forEach
import androidx.lifecycle.LiveData
import com.artemchep.literaryclock.logic.live.MomentLiveData
import com.artemchep.literaryclock.logic.live.QuoteLiveData
import com.artemchep.literaryclock.logic.live.TimeLiveData
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.database.Repo
import com.artemchep.literaryclock.database.RepoImpl
import com.artemchep.literaryclock.database.models.Moment
import com.artemchep.literaryclock.database.models.Quote
import io.realm.Realm
import io.realm.RealmList
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.*
import org.solovyev.android.checkout.Billing

/**
 * @author Artem Chepurnoy
 */
class Heart : Application(), KodeinAware {

    companion object {

        const val NOTIFICATION_WIDGET_UPDATE_SERVICE = 100

        const val UID_WIDGET_UPDATE_JOB = "job::widget_update"

        const val PI_OPEN_MAIN_SCREEN = 1
        const val PI_UPDATE_WIDGET = 2

        const val ACTION_UPDATE_WIDGET = "com.artemchep.literaryclock.UPDATE_WIDGET"

    }

    override val kodein = Kodein.lazy {
        bind<Repo>() with provider { RepoImpl() }

        /*
         * Provides a time live data that actually represents
         * the current time.
         */
        bind<LiveData<Time>>() with singleton { TimeLiveData(this@Heart) }

        /*
         * Provides a moment live data that actually represents
         * the moment of a current time.
         */
        bind<LiveData<MomentItem>>() with multiton { time: LiveData<Time> ->
            val repo by kodein.instance<Repo>()
            return@multiton MomentLiveData(repo, time)
        }

        bind<LiveData<QuoteItem>>() with multiton { moment: LiveData<MomentItem> ->
            return@multiton QuoteLiveData(moment)
        }
    }

    val billing = Billing(this, object : Billing.DefaultConfiguration() {

        override fun getPublicKey(): String {
            return "Your public key, don't forget about encryption"
        }

    })

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Cfg.init(this)

        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val list = ArrayList<Moment>()
            store2.forEach { key, value ->
                Log.d("brrr", "keey=$key")
                val x = Moment().apply {
                    this.key = key
                    this.quotes = RealmList()
                    value.forEach {
                        this.quotes.add(Quote().apply {
                            this.key = it.hashCode()
                            this.title = it.title
                            this.author = it.author
                            this.quote = it.quote
                            this.asin = it.asin
                        })
                    }
                }

                list.add(x)
            }

            it.insertOrUpdate(list)
        }
    }

}
