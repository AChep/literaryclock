package com.artemchep.literaryclock.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moments")
data class MomentEntity(
    @PrimaryKey
    val key: Int,
)
