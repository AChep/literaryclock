package com.artemchep.literaryclock.database

import com.artemchep.literaryclock.database.models.Moment
import com.artemchep.literaryclock.database.models.Quote
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.store.factory.MomentItemFactory
import io.realm.Realm
import io.realm.RealmList
import io.realm.kotlin.where

/**
 * @author Artem Chepurnoy
 */
class RepoImpl : Repo {

    companion object {
        private val emptyMoment = Moment().apply {
            key = 0
            quotes = RealmList(Quote()
                .apply {
                    key = ""
                    quote =
                            "There's no quote for this time yet. Try connecting to internet for database to sync."
                    title = ""
                    author = ""
                    asin = ""
                })
        }
    }

    override fun getMoments(range: ClosedRange<Time>): List<MomentItem> {
        val realm = Realm.getDefaultInstance()
        // Find all of the moments
        // within the range.
        return realm.where<Moment>()
            .greaterThanOrEqualTo(Moment::key.name, range.start.time)
            .lessThanOrEqualTo(Moment::key.name, range.endInclusive.time)
            .sort(Moment::key.name)
            .findAll()
            // Map objects to UI models
            .let { moments ->
                (range.start.time..range.endInclusive.time)
                    .map { time ->
                        moments.find { it.key == time } ?: emptyMoment
                    }
            }
            .map(MomentItemFactory::transform)
            // Close the realm before returning
            // an object.
            .apply {
                realm.close()
            }
    }

}