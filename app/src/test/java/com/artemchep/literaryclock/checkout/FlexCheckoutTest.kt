package com.artemchep.literaryclock.checkout

import android.content.Intent
import android.content.IntentSender
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.solovyev.android.checkout.Billing
import org.solovyev.android.checkout.IntentStarter

@RunWith(RobolectricTestRunner::class)
class FlexCheckoutTest {

    @Test
    fun withIntentStarterClearsStarterWhenBlockThrows() {
        val checkout = FlexCheckout("test", mock<Billing>())
        val starter = mock<IntentStarter>()

        assertThrows(IllegalStateException::class.java) {
            checkout.withIntentStarter(starter) {
                error("boom")
            }
        }

        assertThat(checkout.intentStarter).isNull()
        checkout.withIntentStarter(starter) {
            assertThat(checkout.intentStarter === starter).isTrue()
        }
    }

    @Test
    fun startForResultClearsStarterAfterDelegatingLaunch() {
        val checkout = FlexCheckout("test", mock<Billing>())
        val starter = mock<IntentStarter>()
        val intentSender = mock<IntentSender>()
        val intent = Intent()

        checkout.intentStarter = starter
        checkout.startForResult(intentSender, requestCode = 7, intent)

        verify(starter).startForResult(intentSender, 7, intent)
        assertThat(checkout.intentStarter).isNull()
    }
}
