package com.artemchep.literaryclock.logic.live

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.artemchep.literaryclock.data.DatabaseState
import com.artemchep.literaryclock.data.Repo
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.Time

/**
 * @author Artem Chepurnoy
 */
class MomentLiveData(
    private val repo: Repo,
    databaseStateLiveData: LiveData<DatabaseState>,
    /**
     * Omits the time of the shown
     * moment.
     */
    private val timeLiveData: LiveData<Time>
) : MediatorLiveData<MomentItem>() {

    companion object {
        /**
         * Defines how many moments it retrieves at
         * once from the database.
         */
        const val RANGE_SIZE = 5 // 5 minutes
    }

    init {
        addSource(timeLiveData, ::processTime)
        addSource(databaseStateLiveData, ::processDatabaseState)
    }

    private val rangeInvalid = Time(-1).let { it..it }

    /**
     * Current range of [moments] that we
     * hold.
     */
    private var range: ClosedRange<Time> = rangeInvalid

    private lateinit var moments: List<MomentItem>

    @UiThread
    private fun processTime(time: Time) {
        value = if (time in range) {
            // Current list of moments should contain
            // required moment.
            moments
        } else {
            range = time..Time(time.time + RANGE_SIZE)
            loadMoments()
        }.let { moments ->
            val offset = time.time - range.start.time
            return@let moments[offset]
        }
    }

    @UiThread
    private fun processDatabaseState(state: DatabaseState) {
        val time = timeLiveData.value ?: return

        // Reset the range of cache, so it reloads moments
        // from the database.
        range = rangeInvalid
        processTime(time)
    }

    private fun loadMoments() = repo.getMoments(range)
        .also {
            // Remember the list of moments that we
            // retrieved.
            moments = it
        }

}
