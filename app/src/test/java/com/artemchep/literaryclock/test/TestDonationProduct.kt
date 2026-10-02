package com.artemchep.literaryclock.test

import com.artemchep.literaryclock.SKU_1
import com.artemchep.literaryclock.billing.DonationProduct
import com.artemchep.literaryclock.billing.DonationState

fun testDonationProduct(
    formattedPrice: String = "$1.00",
    priceAmountMicros: Long = 1_000_000L,
    state: DonationState = DonationState.AVAILABLE,
) = DonationProduct(
    id = SKU_1,
    title = "Donation",
    description = "Support development",
    formattedPrice = formattedPrice,
    priceAmountMicros = priceAmountMicros,
    currencyCode = "USD",
    state = state,
    canPurchase = state == DonationState.AVAILABLE,
)
