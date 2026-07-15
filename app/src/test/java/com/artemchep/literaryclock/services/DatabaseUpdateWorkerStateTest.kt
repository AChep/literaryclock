package com.artemchep.literaryclock.services

import android.content.Context
import android.content.ContextWrapper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestListenableWorkerBuilder
import com.artemchep.literaryclock.messageLiveEvent
import com.artemchep.literaryclock.models.Message
import com.artemchep.literaryclock.models.MessageType
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DatabaseUpdateWorkerStateTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @After
    fun tearDown() {
        val worker = newWorker()
        while (DatabaseUpdateWorker.isRunning) {
            worker.setRunningState(false)
        }
    }

    @Test
    fun staysRunningUntilAllOverlappingWorkersFinish() {
        val firstWorker = newWorker()
        val secondWorker = newWorker()

        firstWorker.setRunningState(true)
        secondWorker.setRunningState(true)

        firstWorker.setRunningState(false)

        assertThat(DatabaseUpdateWorker.isRunning).isTrue()

        secondWorker.setRunningState(false)

        assertThat(DatabaseUpdateWorker.isRunning).isFalse()
    }

    @Test
    fun returnsFailureAndPostsMessageWhenUpdateFails() = runBlocking {
        val worker = newWorker(nonHeartApplicationContext())
        val messages = mutableListOf<Message>()
        val observer = Observer<Message>(messages::add)

        messageLiveEvent.observeForever(observer)
        try {
            assertThat(worker.doWork()).isEqualTo(Result.failure())
        } finally {
            messageLiveEvent.removeObserver(observer)
        }

        assertThat(messages.single().type).isEqualTo(MessageType.ERROR)
        assertThat(DatabaseUpdateWorker.isRunning).isFalse()
    }

    private fun newWorker(context: Context = this.context): DatabaseUpdateWorker =
        TestListenableWorkerBuilder<DatabaseUpdateWorker>(context).build()

    private fun nonHeartApplicationContext(): Context =
        object : ContextWrapper(context) {
            override fun getApplicationContext(): Context = this
        }

    private fun DatabaseUpdateWorker.setRunningState(isRunning: Boolean) {
        val method = DatabaseUpdateWorker::class.java.getDeclaredMethod(
            "setState",
            Boolean::class.javaPrimitiveType,
        )
        method.isAccessible = true
        method.invoke(this, isRunning)
    }
}
