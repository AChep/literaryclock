package com.artemchep.literaryclock.services

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.testing.TestListenableWorkerBuilder
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DatabaseUpdateWorkerStateTest {
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

    private fun newWorker(): DatabaseUpdateWorker =
        TestListenableWorkerBuilder<DatabaseUpdateWorker>(context).build()

    private fun DatabaseUpdateWorker.setRunningState(isRunning: Boolean) {
        val method = DatabaseUpdateWorker::class.java.getDeclaredMethod(
            "setState",
            Boolean::class.javaPrimitiveType,
        )
        method.isAccessible = true
        method.invoke(this, isRunning)
    }
}
