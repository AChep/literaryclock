package com.artemchep.literaryclock.data.room

import android.content.Context
import androidx.room.Room
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        MomentEntity::class,
        QuoteEntity::class,
        FavoriteQuoteEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class LiteraryClockDatabase : RoomDatabase() {
    abstract fun literaryClockDao(): LiteraryClockDao

    companion object {
        const val NAME = "literaryclock.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `favorite_quotes` (
                        `quoteKey` TEXT NOT NULL,
                        `favoritedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`quoteKey`),
                        FOREIGN KEY(`quoteKey`) REFERENCES `quotes`(`key`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_favorite_quotes_favoritedAt`
                    ON `favorite_quotes` (`favoritedAt`)
                    """.trimIndent(),
                )
            }
        }

        fun create(context: Context): LiteraryClockDatabase =
            Room.databaseBuilder(
                context,
                LiteraryClockDatabase::class.java,
                NAME,
            )
                .addMigrations(MIGRATION_1_2)
                .build()
    }
}
