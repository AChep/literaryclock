package com.artemchep.literaryclock.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import androidx.lifecycle.LiveData

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

    @Transaction
    @Query("SELECT * FROM favorite_quotes ORDER BY favoritedAt DESC")
    fun observeFavorites(): LiveData<List<FavoriteQuoteWithQuote>>

    @Query("SELECT quoteKey FROM favorite_quotes")
    fun observeFavoriteQuoteKeys(): LiveData<List<String>>

    @Query("SELECT `key` FROM quotes")
    suspend fun getAllQuoteKeys(): List<String>

    @Query("SELECT `key` FROM moments")
    suspend fun getAllMomentKeys(): List<Int>

    @Upsert
    suspend fun upsertMoments(moments: List<MomentEntity>)

    @Upsert
    suspend fun upsertQuotes(quotes: List<QuoteEntity>)

    @Upsert
    suspend fun upsertFavorite(favorite: FavoriteQuoteEntity)

    @Query("DELETE FROM quotes")
    suspend fun deleteAllQuotes()

    @Query("DELETE FROM moments")
    suspend fun deleteAllMoments()

    @Query("DELETE FROM quotes WHERE `key` IN (:keys)")
    suspend fun deleteQuotesByKeys(keys: List<String>)

    @Query("DELETE FROM moments WHERE `key` IN (:keys)")
    suspend fun deleteMomentsByKeys(keys: List<Int>)

    @Query("DELETE FROM favorite_quotes WHERE quoteKey = :quoteKey")
    suspend fun deleteFavoriteByQuoteKey(quoteKey: String)
}
