package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.artemchep.literaryclock.data.room.FavoriteQuoteEntity
import com.artemchep.literaryclock.data.room.FavoriteQuoteWithQuote
import com.artemchep.literaryclock.data.room.QuoteEntity
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.test.FakeLiteraryClockDao
import com.artemchep.literaryclock.test.MainDispatcherRule
import com.artemchep.literaryclock.test.RecordingAnalyticsMain
import com.artemchep.literaryclock.test.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class FavoritesViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = ApplicationProvider.getApplicationContext<Application>()
    private val analytics = RecordingAnalyticsMain()
    private val dao = FakeLiteraryClockDao()
    private val dispatcher = UnconfinedTestDispatcher()

    private fun createViewModel(favorites: List<FavoriteQuoteWithQuote> = emptyList()) =
        FavoritesViewModel(
            application = application,
            analytics = analytics,
            dao = dao,
            favoritesSource = androidx.lifecycle.MutableLiveData(favorites),
            favoriteMutationDispatcher = dispatcher,
            currentTimeMillis = { 4321L },
        )

    @Test
    fun favoritesLiveDataMapsEntitiesToFavoriteQuoteItems() {
        val favorite = FavoriteQuoteWithQuote(
            favorite = FavoriteQuoteEntity(
                quoteKey = "quote-1",
                favoritedAt = 999L,
            ),
            quote = QuoteEntity(
                key = "quote-1",
                quote = "quote body",
                title = "title",
                author = "author",
                asin = "ASIN-1",
                momentKey = 1,
            ),
        )

        val viewModel = createViewModel(listOf(favorite))
        val mappedQuotes = viewModel.favoritesLiveData.getOrAwaitValue()

        assertThat(mappedQuotes).hasSize(1)
        assertThat(mappedQuotes.single().key).isEqualTo("quote-1")
        assertThat(mappedQuotes.single().isFavorite).isTrue()
        assertThat(mappedQuotes.single().quote).isEqualTo("quote body")
    }

    @Test
    fun onFavoritesScreenOpenedLogsAnalytics() {
        val viewModel = createViewModel()

        viewModel.onFavoritesScreenOpened()

        assertThat(analytics.favoritesOpenedCount).isEqualTo(1)
    }

    @Test
    fun toggleFavoriteDeletesFavoriteAndLogsRemovalAnalytics() {
        val viewModel = createViewModel()
        val quote = quoteItem(
            key = "quote-1",
            asin = "ASIN-1",
            isFavorite = true,
        )

        viewModel.toggleFavorite(quote)

        assertThat(dao.deletedFavoriteQuoteKeys).containsExactly("quote-1")
        assertThat(analytics.favoriteRemovedQuotes).containsExactly(quote)
    }

    @Test
    fun openQuoteIgnoresPlaceholderQuotes() {
        val viewModel = createViewModel()
        val quote = quoteItem(
            key = "quote-1",
            asin = "ASIN-1",
            isPlaceholder = true,
        )

        viewModel.openQuote(quote)

        assertThat(viewModel.openUrlEvent.value).isNull()
        assertThat(analytics.openedQuotes).isEmpty()
    }

    private fun quoteItem(
        key: String,
        asin: String,
        isFavorite: Boolean = false,
        isPlaceholder: Boolean = false,
    ) = QuoteItem(
        key = key,
        quote = "quote-$key",
        title = "title-$key",
        asin = asin,
        author = "author-$key",
        isFavorite = isFavorite,
        isPlaceholder = isPlaceholder,
    )
}
