package com.artemchep.literaryclock.logic.live

import android.app.Application
import android.content.Intent
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.utils.currentTime
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import java.util.TimeZone

@RunWith(RobolectricTestRunner::class)
class TimeLiveDataTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val context = ApplicationProvider.getApplicationContext<Application>()

    @Test
    fun timezoneChangedBroadcastUpdatesCurrentTime() {
        val originalTimeZone = TimeZone.getDefault()
        val liveData = TimeLiveData(context)
        val values = mutableListOf<Int>()
        val observer = Observer<Time> { values += it.time }
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
            liveData.observeForever(observer)

            TimeZone.setDefault(TimeZone.getTimeZone("GMT+04:00"))
            context.sendBroadcast(Intent(Intent.ACTION_TIMEZONE_CHANGED))
            shadowOf(Looper.getMainLooper()).idle()

            assertThat(values.last()).isEqualTo(currentTime.time)
        } finally {
            liveData.removeObserver(observer)
            TimeZone.setDefault(originalTimeZone)
        }
    }
}
