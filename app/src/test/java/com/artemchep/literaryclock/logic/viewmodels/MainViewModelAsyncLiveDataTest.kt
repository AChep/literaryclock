package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.artemchep.literaryclock.data.DatabaseState
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.test.FakeLiteraryClockDao
import com.artemchep.literaryclock.test.RecordingAnalyticsMain
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class MainViewModelAsyncLiveDataTest {
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
    fun editTimeUsesLatestCustomTimeImmediately() {
        val viewModel = createViewModel()
        val mainLooper = shadowOf(android.os.Looper.getMainLooper())
        val observedTimes = mutableListOf<Time>()
        val editedTimes = mutableListOf<Time>()
        val timeObserver = Observer<Time> { observedTimes += it }
        val editObserver = Observer<Time> { editedTimes += it }

        viewModel.timeLiveData.observeForever(timeObserver)
        viewModel.editTimeEvent.observeForever(editObserver)
        try {
            mainLooper.idle()
            assertThat(observedTimes).containsExactly(Time(600))

            viewModel.postTime(Time(610))
            viewModel.editTime()
            mainLooper.idle()

            assertThat(observedTimes).containsExactly(Time(600), Time(610)).inOrder()
            assertThat(editedTimes).containsExactly(Time(610))
        } finally {
            viewModel.timeLiveData.removeObserver(timeObserver)
            viewModel.editTimeEvent.removeObserver(editObserver)
        }
    }
}
