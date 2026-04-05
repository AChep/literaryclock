package com.artemchep.literaryclock.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface LiteraryClockDao {
    @Transaction
    @Query(
        """
        SELECT * FROM moments
        WHERE `key` >= :start AND `key` <= :end
        ORDER BY `key`
        """,
    )
    suspend fun getMoments(start: Int, end: Int): List<MomentWithQuotes>

    @Query("SELECT COUNT(*) FROM moments")
    suspend fun countMoments(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoments(moments: List<MomentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<QuoteEntity>)

    @Query("DELETE FROM quotes")
    suspend fun deleteAllQuotes()

    @Query("DELETE FROM moments")
    suspend fun deleteAllMoments()
}
