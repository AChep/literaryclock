package com.artemchep.literaryclock.logic.live

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.checkout.FlexCheckout
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.solovyev.android.checkout.Checkout

@RunWith(RobolectricTestRunner::class)
class CheckoutLiveDataTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val application = ApplicationProvider.getApplicationContext<Heart>()

    @Test
    fun emittedCheckoutIsStartedBeforeObserversReceiveIt() {
        val liveData = CheckoutLiveData(application)
        var whenReadyFailure: Throwable? = null
        val observer = Observer<FlexCheckout> { checkout ->
            try {
                checkout.whenReady(object : Checkout.EmptyListener() {})
            } catch (e: Throwable) {
                whenReadyFailure = e
            }
        }

        liveData.observeForever(observer)
        try {
            assertThat(whenReadyFailure).isNull()
        } finally {
            liveData.removeObserver(observer)
        }
    }
}
