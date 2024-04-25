package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.analytics.AnalyticsMain
import com.artemchep.literaryclock.logic.SingleLiveEvent
import com.artemchep.literaryclock.logic.live.DatabaseStateLiveData
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.utils.ext.observeOnce
import org.kodein.di.instance

/**
 * @author Artem Chepurnoy
 */
class MainViewModel(application: Application) : BaseViewModel(application) {

    private val analytics by instance<AnalyticsMain>()

    val shareQuoteEvent = SingleLiveEvent<QuoteItem>()

    val editTimeEvent = SingleLiveEvent<Time>()

    val openUrlEvent = SingleLiveEvent<String>()

    private val currentTimeLiveData by instance<LiveData<Time>>()

    val customTimeLiveData = MutableLiveData<Time>()

    val timeLiveData = MediatorLiveData<Time>()
        .apply {
            val resolver = Observer<Time> { _ ->
                val time =
                    customTimeLiveData.value?.takeUnless { it.time < 0 }
                        ?: currentTimeLiveData.value
                time?.let(::postValue)
            }

            addSource(currentTimeLiveData, resolver)
            addSource(customTimeLiveData, resolver)
        }

    val databaseIsUpdatingLiveData = DatabaseStateLiveData(application)

    val momentLiveData by instance<LiveData<Time>, LiveData<MomentItem>>(arg = timeLiveData)

    /**
     * Sets the custom time if the time is not equal to the [current time][currentTimeLiveData],
     * otherwise clears custom time.
     */
    @UiThread
    fun postTime(time: Time) {
        val value = time.takeUnless { currentTimeLiveData.value == it } ?: Time(-1)
        customTimeLiveData.value = value
    }

    fun editTime() {
        timeLiveData.observeOnce(editTimeEvent::postValue)
    }

    @UiThread
    fun openQuote(quote: QuoteItem) {
        "http://www.amazon.com/dp/${quote.asin}".let(openUrlEvent::setValue)
        analytics.logQuoteOpen(quote)
    }

    @UiThread
    fun shareQuote(quote: QuoteItem) {
        quote.let(shareQuoteEvent::setValue)
        analytics.logQuoteShare(quote)
    }

}
