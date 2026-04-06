package com.artemchep.literaryclock.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_quotes",
    foreignKeys = [
        ForeignKey(
            entity = QuoteEntity::class,
            parentColumns = ["key"],
            childColumns = ["quoteKey"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("favoritedAt"),
    ],
)
data class FavoriteQuoteEntity(
    @PrimaryKey
    val quoteKey: String,
    val favoritedAt: Long,
)
