package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import androidx.annotation.UiThread
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.artemchep.literaryclock.analytics.AnalyticsMain
import com.artemchep.literaryclock.data.room.FavoriteQuoteEntity
import com.artemchep.literaryclock.data.room.LiteraryClockDao
import com.artemchep.literaryclock.logic.SingleLiveEvent
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.store.factory.QuoteItemFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.instance

class FavoritesViewModel(application: Application) : BaseViewModel(application) {

    private val analytics by instance<AnalyticsMain>()
    private val dao by instance<LiteraryClockDao>()

    val shareQuoteEvent = SingleLiveEvent<QuoteItem>()

    val openUrlEvent = SingleLiveEvent<String>()

    val favoritesLiveData = dao.observeFavorites().map { favorites ->
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
