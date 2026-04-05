package com.artemchep.literaryclock.data.room

import androidx.room.Embedded
import androidx.room.Relation

data class MomentWithQuotes(
    @Embedded
    val moment: MomentEntity,
    @Relation(
        parentColumn = "key",
        entityColumn = "momentKey",
    )
    val quotes: List<QuoteEntity>,
)
