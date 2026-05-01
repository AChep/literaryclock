package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.analytics.AnalyticsMain
import com.artemchep.literaryclock.data.room.FavoriteQuoteEntity
import com.artemchep.literaryclock.data.room.FavoriteQuoteWithQuote
import com.artemchep.literaryclock.data.room.LiteraryClockDao
import com.artemchep.literaryclock.logic.SingleLiveEvent
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.store.factory.QuoteItemFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.direct
import org.kodein.di.instance

class FavoritesViewModel internal constructor(
    application: Application,
    private val analytics: AnalyticsMain,
    private val dao: LiteraryClockDao,
    favoritesSource: LiveData<List<FavoriteQuoteWithQuote>>,
    private val favoriteMutationDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis,
) : BaseViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        analytics = (application as Heart).di.direct.instance<AnalyticsMain>(),
        dao = (application as Heart).di.direct.instance<LiteraryClockDao>(),
        favoritesSource = (application as Heart).di.direct
            .instance<LiteraryClockDao>()
            .observeFavorites(),
    )

    val shareQuoteEvent = SingleLiveEvent<QuoteItem>()

    val openUrlEvent = SingleLiveEvent<String>()

    val favoritesLiveData = favoritesSource.map { favorites ->
        favorites.map { favorite ->
            QuoteItemFactory.transform(
                origin = favorite.quote,
                isFavorite = true,
            )
        }
    }

    fun onFavoritesScreenOpened() {
        analytics.logFavoritesOpen()
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

        viewModelScope.launch(favoriteMutationDispatcher) {
            if (quote.isFavorite) {
                dao.deleteFavoriteByQuoteKey(quote.key)
            } else {
                dao.upsertFavorite(
                    FavoriteQuoteEntity(
                        quoteKey = quote.key,
                        favoritedAt = currentTimeMillis(),
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
