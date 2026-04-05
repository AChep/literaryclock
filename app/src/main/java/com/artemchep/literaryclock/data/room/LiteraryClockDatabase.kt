package com.artemchep.literaryclock.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        MomentEntity::class,
        QuoteEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class LiteraryClockDatabase : RoomDatabase() {
    abstract fun literaryClockDao(): LiteraryClockDao

    companion object {
        const val NAME = "literaryclock.db"

        fun create(context: Context): LiteraryClockDatabase =
            Room.databaseBuilder(
                context,
                LiteraryClockDatabase::class.java,
                NAME,
            ).build()
    }
}
