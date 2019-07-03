package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.models.Time
import org.kodein.di.generic.instance

/**
 * @author Artem Chepurnoy
 */
class DreamViewModel(application: Application) : BaseViewModel(application) {

    private val currentTimeLiveData by instance<LiveData<Time>>()

    private val momentLiveData by instance<LiveData<Time>, LiveData<MomentItem>>(arg = currentTimeLiveData)

    val quoteLiveData by instance<LiveData<MomentItem>, LiveData<QuoteItem>>(arg = momentLiveData)

}
