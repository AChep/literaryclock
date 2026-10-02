package com.artemchep.literaryclock

import androidx.test.core.app.ApplicationProvider
import com.artemchep.literaryclock.analytics.AnalyticsAbout
import com.artemchep.literaryclock.analytics.AnalyticsDonate
import com.artemchep.literaryclock.analytics.AnalyticsMain
import com.artemchep.literaryclock.billing.DonationBillingRepository
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.kodein.di.direct
import org.kodein.di.instance
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HeartTest {

    private val application = ApplicationProvider.getApplicationContext<Heart>()

    @Test
    fun analyticsBindingsResolveFromApplicationDi() {
        assertThat(application.di.direct.instance<AnalyticsMain>()).isNotNull()
        assertThat(application.di.direct.instance<AnalyticsDonate>()).isNotNull()
        assertThat(application.di.direct.instance<AnalyticsAbout>()).isNotNull()
    }

    @Test
    fun billingRepositoryIsSharedAcrossTheApplication() {
        assertThat(application.di.direct.instance<DonationBillingRepository>())
            .isSameInstanceAs(application.di.direct.instance<DonationBillingRepository>())
    }
}
