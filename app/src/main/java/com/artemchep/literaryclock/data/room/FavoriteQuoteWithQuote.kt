package com.artemchep.literaryclock.data.room

import androidx.room.Embedded
import androidx.room.Relation

data class FavoriteQuoteWithQuote(
    @Embedded
    val favorite: FavoriteQuoteEntity,
    @Relation(
        parentColumn = "quoteKey",
        entityColumn = "key",
    )
    val quote: QuoteEntity,
)
