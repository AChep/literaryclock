package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.models.Time
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

/**
 * @author Artem Chepurnoy
 */
class DreamViewModel(application: Application) : AndroidViewModel(application) {

    private val kodein by closestKodein(application)

    private val currentTimeLiveData by kodein.instance<LiveData<Time>>()

    private val momentLiveData by kodein.instance<LiveData<Time>, LiveData<MomentItem>>(arg = currentTimeLiveData)

    val quoteLiveData by kodein.instance<LiveData<MomentItem>, LiveData<QuoteItem>>(arg = momentLiveData)

}
