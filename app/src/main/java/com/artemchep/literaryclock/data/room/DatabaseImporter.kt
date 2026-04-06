package com.artemchep.literaryclock.data.room

import androidx.room.withTransaction
import com.artemchep.literaryclock.data.firestore.FirestoreQuoteModel
import org.json.JSONArray

class DatabaseImporter(
    private val database: LiteraryClockDatabase,
) {
    companion object {
        private const val SQLITE_IN_LIMIT = 900
    }

    suspend fun importJson(jsonString: String) {
        val quotes = parseQuotes(jsonString)
        val moments = quotes
            .asSequence()
            .map(FirestoreQuoteModel::time)
            .distinct()
            .sorted()
            .map(::MomentEntity)
            .toList()
        val quoteEntities = quotes.map { quote ->
            quote.toEntity(momentKey = quote.time)
        }
        val quoteKeys = quoteEntities.map(QuoteEntity::key)
        val momentKeys = moments.map(MomentEntity::key)

        database.withTransaction {
            val dao = database.literaryClockDao()
            val removedQuoteKeys = if (quoteKeys.isEmpty()) {
                dao.getAllQuoteKeys()
            } else {
                dao.getAllQuoteKeys() - quoteKeys.toSet()
            }
            val removedMomentKeys = if (momentKeys.isEmpty()) {
                dao.getAllMomentKeys()
            } else {
                dao.getAllMomentKeys() - momentKeys.toSet()
            }

            if (moments.isNotEmpty()) {
                dao.upsertMoments(moments)
            }
            if (quoteEntities.isNotEmpty()) {
                dao.upsertQuotes(quoteEntities)
            }

            if (removedQuoteKeys.isNotEmpty()) {
                removedQuoteKeys
                    .chunked(SQLITE_IN_LIMIT)
                    .forEach { keys ->
                        dao.deleteQuotesByKeys(keys)
                    }
            }
            if (quoteEntities.isEmpty()) {
                dao.deleteAllQuotes()
            }

            if (removedMomentKeys.isNotEmpty()) {
                removedMomentKeys
                    .chunked(SQLITE_IN_LIMIT)
                    .forEach { keys ->
                        dao.deleteMomentsByKeys(keys)
                    }
            }
            if (moments.isEmpty()) {
                dao.deleteAllMoments()
            }
        }
    }

    private fun parseQuotes(jsonString: String): List<FirestoreQuoteModel> {
        val jsonArray = JSONArray(jsonString)
        return (0 until jsonArray.length())
            .map { index ->
                val obj = jsonArray.getJSONObject(index)
                FirestoreQuoteModel(
                    key = obj.getString("key"),
                    quote = obj.getString("quote"),
                    title = obj.optString("title"),
                    author = obj.optString("author"),
                    asin = obj.optString("asin"),
                    time = obj.getInt("time"),
                )
            }
    }
}
