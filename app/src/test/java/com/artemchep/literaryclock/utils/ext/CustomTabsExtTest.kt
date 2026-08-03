package com.artemchep.literaryclock.utils.ext

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CustomTabsExtTest {
    @Test
    fun launchInCustomTabsDoesNotThrowWhenNoBrowserCanHandleUrl() {
        val activity = Robolectric.buildActivity(NoBrowserActivity::class.java).setup().get()
        val uri = Uri.parse("https://example.com")

        var thrown: Throwable? = null
        try {
            uri.launchInCustomTabs(activity)
        } catch (e: Throwable) {
            thrown = e
        }

        assertThat(thrown).isNull()
    }

    class NoBrowserActivity : Activity() {
        override fun startActivity(intent: Intent?, options: Bundle?) {
            throw ActivityNotFoundException()
        }
    }
}
