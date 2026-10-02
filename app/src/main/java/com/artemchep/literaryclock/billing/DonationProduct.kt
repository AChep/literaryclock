package com.artemchep.literaryclock.billing

data class DonationProduct(
    val id: String,
    val title: String,
    val description: String,
    val formattedPrice: String,
    val priceAmountMicros: Long,
    val currencyCode: String,
    val state: DonationState,
    val canPurchase: Boolean,
)

enum class DonationState {
    AVAILABLE,
    PENDING,
    PURCHASED,
}
