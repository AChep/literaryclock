package com.artemchep.literaryclock.analytics

import org.solovyev.android.checkout.Sku

/**
 * @author Artem Chepurnoy
 */
interface AnalyticsDonate : Analytics {

    fun logDonateSkuOpen(sku: Sku)

}