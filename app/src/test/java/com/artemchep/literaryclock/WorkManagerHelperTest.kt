package com.artemchep.literaryclock

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.artemchep.literaryclock.services.DatabaseUpdateWorker
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class WorkManagerHelperTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
    }

    @After
    fun tearDown() {
        WorkManager.getInstance(context)
            .cancelAllWork()
            .result
            .get(5, TimeUnit.SECONDS)
        WorkManagerTestInitHelper.closeWorkDatabase()
    }

    @Test
    fun immediateDatabaseUpdateIsNotDuplicated() {
        context.startUpdateDatabaseImmediateJob()
        context.startUpdateDatabaseImmediateJob()

        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosByTag(DatabaseUpdateWorker::class.java.name)
            .get(5, TimeUnit.SECONDS)

        assertThat(workInfos.filterNot { it.state == WorkInfo.State.CANCELLED }).hasSize(1)
    }
}
