package com.artemchep.literaryclock.database.firestore

import com.google.firebase.Timestamp

/**
 * @author Artem Chepurnoy
 */
data class FirestoreBatchModel(
    var timestamp: Timestamp? = null
)
