package com.artemchep.literaryclock.logic.live

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.artemchep.literaryclock.data.DatabaseState
import com.artemchep.literaryclock.data.Repo
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.Time
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var loadJob: Job? = null

    /**
     * Current range of [moments] that we
     * hold.
     */
    private var range: ClosedRange<Time> = rangeInvalid

    private var moments: List<MomentItem> = emptyList()

    @UiThread
    private fun processTime(time: Time) {
        if (time in range && moments.isNotEmpty()) {
            val offset = time.time - range.start.time
            value = moments[offset]
            return
        }

        val requestedRange = time..Time(time.time + RANGE_SIZE)
        loadMoments(requestedRange, time)
    }

    @UiThread
    private fun processDatabaseState(state: DatabaseState) {
        val time = timeLiveData.value ?: return

        // Reset the range of cache, so it reloads moments
        // from the database.
        range = rangeInvalid
        processTime(time)
    }

    private fun loadMoments(
        requestedRange: ClosedRange<Time>,
        requestedTime: Time,
    ) {
        loadJob?.cancel()
        loadJob = scope.launch {
            val loadedMoments = withContext(Dispatchers.IO) {
                repo.getMoments(requestedRange)
            }
            range = requestedRange
            moments = loadedMoments

            val currentTime = timeLiveData.value ?: requestedTime
            if (currentTime in requestedRange && loadedMoments.isNotEmpty()) {
                val offset = currentTime.time - requestedRange.start.time
                value = loadedMoments[offset]
            }
        }
    }

    override fun onInactive() {
        loadJob?.cancel()
        if (!hasActiveObservers()) {
            scope.coroutineContext.cancelChildren()
        }
        super.onInactive()
    }

}
