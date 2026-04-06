package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.analytics.AnalyticsMain
import com.artemchep.literaryclock.data.room.FavoriteQuoteEntity
import com.artemchep.literaryclock.data.room.LiteraryClockDao
import com.artemchep.literaryclock.logic.SingleLiveEvent
import com.artemchep.literaryclock.logic.live.DatabaseStateLiveData
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.utils.ext.observeOnce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.instance

/**
 * @author Artem Chepurnoy
 */
class MainViewModel(application: Application) : BaseViewModel(application) {

    private val analytics by instance<AnalyticsMain>()
    private val dao by instance<LiteraryClockDao>()

    val shareQuoteEvent = SingleLiveEvent<QuoteItem>()

    val editTimeEvent = SingleLiveEvent<Time>()

    val openUrlEvent = SingleLiveEvent<String>()

    private val currentTimeLiveData by instance<LiveData<Time>>(
        tag = Heart.TAG_LD_TIME,
    )

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

    private val rawMomentLiveData by instance<LiveData<Time>, LiveData<MomentItem>>(
        arg = timeLiveData,
        tag = Heart.TAG_LD_MOMENT_ITEM,
    )
    private val favoriteQuoteKeysLiveData = dao.observeFavoriteQuoteKeys().map {
        it.toSet()
    }
    val momentLiveData = MediatorLiveData<MomentItem>()
        .apply {
            fun publishMoment() {
                val moment = rawMomentLiveData.value ?: return
                val favoriteQuoteKeys = favoriteQuoteKeysLiveData.value.orEmpty()
                value = moment.copy(
                    quotes = moment.quotes.map { quote ->
                        quote.copy(
                            isFavorite = !quote.isPlaceholder && quote.key in favoriteQuoteKeys,
                        )
                    },
                )
            }

            addSource(rawMomentLiveData) { publishMoment() }
            addSource(favoriteQuoteKeysLiveData) { publishMoment() }
        }

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
        if (quote.isPlaceholder || quote.asin.isBlank()) {
            return
        }

        "http://www.amazon.com/dp/${quote.asin}".let(openUrlEvent::setValue)
        analytics.logQuoteOpen(quote)
    }

    @UiThread
    fun shareQuote(quote: QuoteItem) {
        if (quote.isPlaceholder) {
            return
        }

        quote.let(shareQuoteEvent::setValue)
        analytics.logQuoteShare(quote)
    }

    @UiThread
    fun toggleFavorite(quote: QuoteItem) {
        if (quote.isPlaceholder || quote.key.isBlank()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            if (quote.isFavorite) {
                dao.deleteFavoriteByQuoteKey(quote.key)
            } else {
                dao.upsertFavorite(
                    FavoriteQuoteEntity(
                        quoteKey = quote.key,
                        favoritedAt = System.currentTimeMillis(),
                    ),
                )
            }
        }

        if (quote.isFavorite) {
            analytics.logQuoteFavoriteRemove(quote)
        } else {
            analytics.logQuoteFavoriteAdd(quote)
        }
    }

}
