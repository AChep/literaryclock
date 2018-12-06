package com.artemchep.literaryclock.logic.live

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.QuoteItem

/**
 * @author Artem Chepurnoy
 */
class QuoteLiveData(
    momentLiveData: LiveData<MomentItem>
) : MediatorLiveData<QuoteItem>() {

    init {
        addSource(momentLiveData, ::processMoment)
    }

    private fun processMoment(moment: MomentItem) {
        value = moment.quotes.random()
    }

}
