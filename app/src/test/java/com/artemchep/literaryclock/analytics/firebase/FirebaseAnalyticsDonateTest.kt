package com.artemchep.literaryclock.analytics.firebase

import com.google.firebase.analytics.FirebaseAnalytics
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.solovyev.android.checkout.Sku

@RunWith(RobolectricTestRunner::class)
class FirebaseAnalyticsDonateTest {
    @Test
    fun logDonateSkuOpenConvertsPriceMicrosToCurrencyUnits() {
        val firebaseAnalytics = mock<FirebaseAnalytics>()
        val analytics = FirebaseAnalyticsDonate(firebaseAnalytics)
        val sku = Sku(
            "inapp",
            "donation",
            "$7.99",
            Sku.Price(7_990_000L, "USD"),
            "Donation",
            "Support development",
            "",
            Sku.Price.EMPTY,
            "",
            "",
            "",
            0,
        )

        analytics.logDonateSkuOpen(sku)

        val bundleCaptor = argumentCaptor<android.os.Bundle>()
        verify(firebaseAnalytics).logEvent(
            eq(FirebaseAnalytics.Event.BEGIN_CHECKOUT),
            bundleCaptor.capture(),
        )
        val bundle = bundleCaptor.firstValue
        assertEquals(7.99, bundle.getDouble(FirebaseAnalytics.Param.VALUE), 0.0)
        assertEquals("USD", bundle.getString(FirebaseAnalytics.Param.CURRENCY))
    }
}
