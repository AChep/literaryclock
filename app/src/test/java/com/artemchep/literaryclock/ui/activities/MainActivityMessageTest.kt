package com.artemchep.literaryclock.ui.activities

import android.content.Context
import android.widget.Toast
import androidx.test.core.app.ApplicationProvider
import androidx.work.testing.WorkManagerTestInitHelper
import com.artemchep.literaryclock.messageLiveEvent
import com.artemchep.literaryclock.models.Message
import com.artemchep.literaryclock.models.MessageType
import com.google.common.truth.Truth.assertThat
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

    @Test
    fun displaysEachMessageTypeAsToast() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        var previousToast: Toast? = null

        for (type in MessageType.values()) {
            val text = "message-$type"
            messageLiveEvent.value = Message(type) { text }

            val displayedToast = ShadowToast.getLatestToast()
            assertThat(displayedToast).isNotNull()
            assertThat(displayedToast).isNotSameInstanceAs(previousToast)
            previousToast = displayedToast
        }

        activity.finish()
    }
}
