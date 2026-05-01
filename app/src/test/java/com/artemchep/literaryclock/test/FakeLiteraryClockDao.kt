package com.artemchep.literaryclock.test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artemchep.literaryclock.data.room.FavoriteQuoteEntity
import com.artemchep.literaryclock.data.room.FavoriteQuoteWithQuote
import com.artemchep.literaryclock.data.room.LiteraryClockDao
import com.artemchep.literaryclock.data.room.MomentEntity
import com.artemchep.literaryclock.data.room.MomentWithQuotes
import com.artemchep.literaryclock.data.room.QuoteEntity

class FakeLiteraryClockDao(
    favoriteQuoteKeys: List<String> = emptyList(),
    favorites: List<FavoriteQuoteWithQuote> = emptyList(),
) : LiteraryClockDao {
    val favoriteQuoteKeysLiveData = MutableLiveData(favoriteQuoteKeys)
    val favoritesLiveData = MutableLiveData(favorites)

    val upsertedFavorites = mutableListOf<FavoriteQuoteEntity>()
    val deletedFavoriteQuoteKeys = mutableListOf<String>()

    override suspend fun getMoments(start: Int, end: Int): List<MomentWithQuotes> {
        throw NotImplementedError()
    }

    override suspend fun countMoments(): Int {
        throw NotImplementedError()
    }

    override fun observeFavorites(): LiveData<List<FavoriteQuoteWithQuote>> = favoritesLiveData

    override fun observeFavoriteQuoteKeys(): LiveData<List<String>> = favoriteQuoteKeysLiveData

    override suspend fun isFavoriteQuote(quoteKey: String): Boolean =
        favoriteQuoteKeysLiveData.value.orEmpty().contains(quoteKey)

    override suspend fun getQuoteByKey(quoteKey: String): QuoteEntity? =
        favoritesLiveData.value
            .orEmpty()
            .firstOrNull { it.quote.key == quoteKey }
            ?.quote

    override suspend fun getAllQuoteKeys(): List<String> {
        throw NotImplementedError()
    }

    override suspend fun getAllMomentKeys(): List<Int> {
        throw NotImplementedError()
    }

    override suspend fun upsertMoments(moments: List<MomentEntity>) {
        throw NotImplementedError()
    }

    override suspend fun upsertQuotes(quotes: List<QuoteEntity>) {
        throw NotImplementedError()
    }

    override suspend fun upsertFavorite(favorite: FavoriteQuoteEntity) {
        upsertedFavorites += favorite
        favoriteQuoteKeysLiveData.value = favoriteQuoteKeysLiveData.value.orEmpty() + favorite.quoteKey
    }

    override suspend fun deleteAllQuotes() {
        throw NotImplementedError()
    }

    override suspend fun deleteAllMoments() {
        throw NotImplementedError()
    }

    override suspend fun deleteQuotesByKeys(keys: List<String>) {
        throw NotImplementedError()
    }

    override suspend fun deleteMomentsByKeys(keys: List<Int>) {
        throw NotImplementedError()
    }

    override suspend fun deleteFavoriteByQuoteKey(quoteKey: String) {
        deletedFavoriteQuoteKeys += quoteKey
        favoriteQuoteKeysLiveData.value = favoriteQuoteKeysLiveData.value.orEmpty() - quoteKey
    }
}
