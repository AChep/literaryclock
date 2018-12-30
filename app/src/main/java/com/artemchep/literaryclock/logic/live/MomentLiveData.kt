package com.artemchep.literaryclock.logic.live

import android.content.Context
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.artemchep.literaryclock.database.Repo
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.Time

/**
 * @author Artem Chepurnoy
 */
class MomentLiveData(
    context: Context,
    private val repo: Repo,
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

        // Refresh the data when the state of the database
        // changes.
        val dbStateLiveData = DatabaseIsUpdatingLiveData(context)
        addSource(dbStateLiveData) {
            refresh()
        }
    }

    /**
     * Current range of [moments] that we
     * hold.
     */
    private var range: ClosedRange<Time> = Time(-1).let { it..it }

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

    private fun refresh() {
        val time = timeLiveData.value ?: return

        // Refresh the loaded
        // moments
        loadMoments()

        // Post an update
        processTime(time)
    }

    private fun loadMoments() = repo.getMoments(range)
        .also {
            // Remember the list of moments that we
            // retrieved.
            moments = it
        }

}
