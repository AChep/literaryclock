package com.artemchep.literaryclock.ui.activities

import android.content.Context
import android.widget.Toast
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.artemchep.literaryclock.messageLiveEvent
import com.artemchep.literaryclock.models.Message
import com.artemchep.literaryclock.models.MessageType
import com.google.common.truth.Truth.assertThat
import java.util.concurrent.TimeUnit
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
class MainActivityMessageTest {
    @Before
    fun setUp() {
        WorkManagerTestInitHelper.initializeTestWorkManager(
            ApplicationProvider.getApplicationContext<Context>()
        )
    }

    @After
    fun tearDown() {
        WorkManager.getInstance(ApplicationProvider.getApplicationContext<Context>())
            .cancelAllWork()
            .result
            .get(5, TimeUnit.SECONDS)
        WorkManagerTestInitHelper.closeWorkDatabase()
    }

    @Test
    fun displaysEachMessageTypeAsToast() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        try {
            var previousToast: Toast? = null

            for (type in MessageType.values()) {
                val text = "message-$type"
                messageLiveEvent.value = Message(type) { text }

                val displayedToast = ShadowToast.getLatestToast()
                assertThat(displayedToast).isNotNull()
                assertThat(displayedToast).isNotSameInstanceAs(previousToast)
                previousToast = displayedToast
            }
        } finally {
            controller.pause().stop().destroy()
        }

        assertThat(messageLiveEvent.hasObservers()).isFalse()
    }
}
