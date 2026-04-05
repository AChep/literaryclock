package com.artemchep.literaryclock.services

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestListenableWorkerBuilder
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.data.room.LiteraryClockDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.kodein.di.direct
import org.kodein.di.instance

@RunWith(AndroidJUnit4::class)
class DatabaseUpdateWorkerTest {
    private lateinit var context: Context
    private lateinit var database: LiteraryClockDatabase

    @Before
    fun setUp() = runBlocking {
        context = ApplicationProvider.getApplicationContext()
        val app = context.applicationContext as Heart
        database = app.di.direct.instance()
        database.clearAllTables()
    }

    @After
    fun tearDown() = runBlocking {
        database.clearAllTables()
    }

    @Test
    fun importsBundledDatabaseAndIsIdempotent() = runBlocking {
        val worker = TestListenableWorkerBuilder<DatabaseUpdateWorker>(context).build()

        assertEquals(Result.success(), worker.doWork())
        assertDatabaseState()

        assertEquals(Result.success(), worker.doWork())
        assertDatabaseState()
    }

    private suspend fun assertDatabaseState() {
        val dao = database.literaryClockDao()
        assertEquals(1440, dao.countMoments())

        val midnightMoment = dao.getMoments(start = 0, end = 0).single()
        assertEquals(0, midnightMoment.moment.key)
        assertTrue(midnightMoment.quotes.isNotEmpty())
        assertTrue(midnightMoment.quotes.all { it.momentKey == 0 })
    }
}
