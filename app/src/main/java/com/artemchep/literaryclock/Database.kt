package com.artemchep.literaryclock

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

/**
 * @author Artem Chepurnoy
 */
open class Segment(val path: String)

/**
 * @author Artem Chepurnoy
 */
open class DocumentSegment(path: String) : Segment(path) {

    val ref: DocumentReference
        get() {
            return FirebaseFirestore.getInstance().document(path)
        }

}

/**
 * @author Artem Chepurnoy
 */
open class CollectionSegment(path: String) : Segment(path) {

    val ref: CollectionReference
        get() {
            return FirebaseFirestore.getInstance().collection(path)
        }

    open fun one(childId: String): DocumentSegment = DocumentSegment("$path/$childId")

}

/**
 * @author Artem Chepurnoy
 */
object Batches : CollectionSegment("/quotes") {

    override fun one(childId: String) = Batch("$path/$childId")

    class Batch(path: String) : DocumentSegment(path) {

        fun quotes() = CollectionSegment("$path/quotes")

    }

}

/**
 * @author Artem Chepurnoy
 */
object Suggestions : CollectionSegment("/suggestions") {
}
