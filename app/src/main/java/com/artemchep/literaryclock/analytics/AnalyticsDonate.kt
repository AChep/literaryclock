package com.artemchep.literaryclock.analytics

import com.artemchep.literaryclock.billing.DonationProduct

/**
 * @author Artem Chepurnoy
 */
interface AnalyticsDonate : Analytics {

    fun logDonateSkuOpen(product: DonationProduct)

}