package com.artemchep.literaryclock.logic.live

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.test.core.app.ApplicationProvider
import androidx.work.testing.TestListenableWorkerBuilder
import com.artemchep.literaryclock.data.DatabaseState
import com.artemchep.literaryclock.services.DatabaseUpdateWorker
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class DatabaseStateLiveDataTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val application = ApplicationProvider.getApplicationContext<Application>()

    @Before
    fun setUp() {
        resetWorkerRunningState()
        resetLocalBroadcastManager()
    }

    @After
    fun tearDown() {
        resetWorkerRunningState()
        resetLocalBroadcastManager()
    }

    private fun resetWorkerRunningState() {
        val worker = newWorker()
        while (DatabaseUpdateWorker.isRunning) {
            worker.setRunningState(false)
        }
    }

    @Test
    fun observesRunningStateIfUpdateStartsDuringActivation() {
        val worker = newWorker()
        var updateStarted = false
        val context = object : ContextWrapper(application) {
            override fun getApplicationContext(): Context {
                if (!updateStarted) {
                    updateStarted = true
                    worker.setRunningState(true)
                }
                return application
            }
        }
        val liveData = DatabaseStateLiveData(context)
        val states = mutableListOf<DatabaseState>()
        val observer = Observer<DatabaseState>(states::add)

        liveData.observeForever(observer)
        try {
            assertThat(states.last()).isEqualTo(DatabaseState.UPDATING)
        } finally {
            liveData.removeObserver(observer)
        }
    }

    @Test
    fun observesRunningStateIfUpdateFinishesBeforeQueuedBroadcastDelivery() {
        val worker = newWorker()
        val liveData = DatabaseStateLiveData(application)
        val states = mutableListOf<DatabaseState>()
        val observer = Observer<DatabaseState>(states::add)

        liveData.observeForever(observer)
        try {
            worker.setRunningState(true)
            worker.setRunningState(false)

            shadowOf(Looper.getMainLooper()).idle()

            assertThat(states).contains(DatabaseState.UPDATING)
        } finally {
            liveData.removeObserver(observer)
        }
    }

    private fun newWorker(context: Context = application): DatabaseUpdateWorker =
        TestListenableWorkerBuilder<DatabaseUpdateWorker>(context).build()

    private fun DatabaseUpdateWorker.setRunningState(isRunning: Boolean) {
        val method = DatabaseUpdateWorker::class.java.getDeclaredMethod(
            "setState",
            Boolean::class.javaPrimitiveType,
        )
        method.isAccessible = true
        method.invoke(this, isRunning)
    }

    private fun resetLocalBroadcastManager() {
        val field = LocalBroadcastManager::class.java.getDeclaredField("mInstance")
        field.isAccessible = true
        field.set(null, null)
    }
}
