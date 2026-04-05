package com.artemchep.literaryclock.data.room

import androidx.room.withTransaction
import com.artemchep.literaryclock.data.firestore.FirestoreQuoteModel
import org.json.JSONArray

class DatabaseImporter(
    private val database: LiteraryClockDatabase,
) {
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

        database.withTransaction {
            val dao = database.literaryClockDao()
            dao.deleteAllQuotes()
            dao.deleteAllMoments()
            dao.insertMoments(moments)
            dao.insertQuotes(quoteEntities)
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
