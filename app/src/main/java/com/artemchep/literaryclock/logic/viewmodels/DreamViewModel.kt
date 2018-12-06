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

    val quoteLiveData = MediatorLiveData<QuoteItem>()
        .apply {
            // Redirect changes from moment live data
            // to a mediator.
            val timeLiveData by kodein.instance<LiveData<Time>>()
            val momentLiveData by kodein.instance<LiveData<Time>, LiveData<MomentItem>>(arg = timeLiveData)
            addSource(momentLiveData) { moment ->
                moment.quotes.random().let(::postValue)
            }
        }

}
