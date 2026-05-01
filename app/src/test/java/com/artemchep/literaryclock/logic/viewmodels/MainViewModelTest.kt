package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.artemchep.literaryclock.data.DatabaseState
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.models.Time
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
class MainViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = ApplicationProvider.getApplicationContext<Application>()
    private val analytics = RecordingAnalyticsMain()
    private val dao = FakeLiteraryClockDao()
    private val currentTimeLiveData = MutableLiveData(Time(600))
    private val databaseStateLiveData = MutableLiveData(DatabaseState.IDLE)
    private val rawMomentLiveData = MutableLiveData<MomentItem>()
    private val dispatcher = UnconfinedTestDispatcher()

    private fun createViewModel() = MainViewModel(
        application = application,
        analytics = analytics,
        dao = dao,
        currentTimeLiveData = currentTimeLiveData,
        databaseIsUpdatingLiveData = databaseStateLiveData,
        rawMomentLiveDataFactory = { rawMomentLiveData },
        favoriteMutationDispatcher = dispatcher,
        currentTimeMillis = { 1234L },
    )

    @Test
    fun postTimeClearsCustomOverrideWhenEqualToCurrentTime() {
        val viewModel = createViewModel()

        viewModel.postTime(Time(600))

        assertThat(viewModel.customTimeLiveData.value).isEqualTo(Time(-1))
    }

    @Test
    fun openQuoteEmitsUrlAndLogsAnalyticsForValidQuote() {
        val viewModel = createViewModel()
        val quote = quoteItem(key = "quote-1", asin = "ASIN-1")

        viewModel.openQuote(quote)

        assertThat(viewModel.openUrlEvent.value).isEqualTo("http://www.amazon.com/dp/ASIN-1")
        assertThat(analytics.openedQuotes).containsExactly(quote)
    }

    @Test
    fun shareQuoteIgnoresPlaceholderQuotes() {
        val viewModel = createViewModel()
        val quote = quoteItem(
            key = "quote-1",
            asin = "ASIN-1",
            isPlaceholder = true,
        )

        viewModel.shareQuote(quote)

        assertThat(viewModel.shareQuoteEvent.value).isNull()
        assertThat(analytics.sharedQuotes).isEmpty()
    }

    @Test
    fun toggleFavoriteUpsertsFavoriteAndLogsAddAnalytics() {
        val viewModel = createViewModel()
        val quote = quoteItem(key = "quote-1", asin = "ASIN-1")

        viewModel.toggleFavorite(quote)

        assertThat(dao.upsertedFavorites).containsExactly(
            com.artemchep.literaryclock.data.room.FavoriteQuoteEntity(
                quoteKey = "quote-1",
                favoritedAt = 1234L,
            ),
        )
        assertThat(analytics.favoriteAddedQuotes).containsExactly(quote)
    }

    @Test
    fun momentLiveDataAppliesFavoriteOverlayOnlyToRealMatchingQuotes() {
        val viewModel = createViewModel()
        val favoriteQuote = quoteItem(key = "favorite-key", asin = "ASIN-1")
        val placeholderQuote = quoteItem(
            key = "favorite-key",
            asin = "ASIN-1",
            isPlaceholder = true,
        )

        val publishedMoment = viewModel.momentLiveData.getOrAwaitValue {
            dao.favoriteQuoteKeysLiveData.value = listOf("favorite-key")
            rawMomentLiveData.value = MomentItem(
                quotes = listOf(
                    favoriteQuote,
                    placeholderQuote,
                ),
            )
        }

        assertThat(publishedMoment.quotes[0].isFavorite).isTrue()
        assertThat(publishedMoment.quotes[1].isFavorite).isFalse()
    }

    private fun quoteItem(
        key: String,
        asin: String,
        isPlaceholder: Boolean = false,
    ) = QuoteItem(
        key = key,
        quote = "quote-$key",
        title = "title-$key",
        asin = asin,
        author = "author-$key",
        isPlaceholder = isPlaceholder,
    )
}
