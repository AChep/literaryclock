package com.artemchep.literaryclock.database

import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.store.factory.MomentItemFactory
import com.artemchep.literaryclock.database.models.Moment
import io.realm.Realm
import io.realm.kotlin.where

/**
 * @author Artem Chepurnoy
 */
class RepoImpl : Repo {

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
            .map {
                MomentItemFactory.transform(it!!)
            }
            // Close the realm before returning
            // an object.
            .apply {
                realm.close()
            }
    }

}