package com.artemchep.literaryclock.database

import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.Time

/**
 * @author Artem Chepurnoy
 */
interface Repo {

    /**
     * Returns sorted list of moments
     * within the range.
     */
    fun getMoments(range: ClosedRange<Time>): List<MomentItem>

}
