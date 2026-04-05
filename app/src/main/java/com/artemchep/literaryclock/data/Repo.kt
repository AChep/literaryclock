package com.artemchep.literaryclock.data

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
    suspend fun getMoments(range: ClosedRange<Time>): List<MomentItem>

}
