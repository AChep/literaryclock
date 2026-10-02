package com.artemchep.literaryclock.logic.viewmodels

import android.app.Activity
import android.app.Application
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.analytics.AnalyticsDonate
import com.artemchep.literaryclock.billing.DonationBillingRepository
import com.artemchep.literaryclock.billing.DonationProduct
import org.kodein.di.direct
import org.kodein.di.instance

class DonateViewModel internal constructor(
    application: Application,
    private val billing: DonationBillingRepository,
    private val analytics: AnalyticsDonate,
) : BaseViewModel(application) {
    constructor(application: Application) : this(
        application,
        (application as Heart).di.direct.instance<DonationBillingRepository>(),
        application.di.direct.instance<AnalyticsDonate>(),
    )

    val productLiveData = billing.products

    fun refresh() = billing.refresh()

    fun purchase(activity: Activity, product: DonationProduct) {
        if (billing.purchase(activity, product.id)) {
            analytics.logDonateSkuOpen(product)
        }
    }
}
