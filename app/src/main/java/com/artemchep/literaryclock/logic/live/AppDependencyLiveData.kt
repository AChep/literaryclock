package com.artemchep.literaryclock.logic.live

import androidx.lifecycle.LiveData
import com.artemchep.literaryclock.BuildConfig
import com.artemchep.literaryclock.models.DependencyItem

/**
 * @author Artem Chepurnoy
 */
class AppDependencyLiveData : LiveData<List<DependencyItem>>() {

    init {
        value = BuildConfig.DEPENDENCIES
            .map {
                DependencyItem(it.name, it.versionName)
            }
    }

}