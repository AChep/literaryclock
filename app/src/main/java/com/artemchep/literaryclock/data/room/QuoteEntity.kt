package com.artemchep.literaryclock.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quotes",
    foreignKeys = [
        ForeignKey(
            entity = MomentEntity::class,
            parentColumns = ["key"],
            childColumns = ["momentKey"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("momentKey")],
)
data class QuoteEntity(
    @PrimaryKey
    val key: String,
    val quote: String,
    val title: String,
    val author: String,
    val asin: String,
    val momentKey: Int,
)
