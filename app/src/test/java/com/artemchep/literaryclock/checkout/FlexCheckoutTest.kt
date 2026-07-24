package com.artemchep.literaryclock.checkout

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
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
}
