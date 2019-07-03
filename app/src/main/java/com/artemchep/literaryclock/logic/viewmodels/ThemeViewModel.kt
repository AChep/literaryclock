package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import com.artemchep.literaryclock.Cfg
import com.artemchep.literaryclock.logic.live.ConfigLiveData

/**
 * @author Artem Chepurnoy
 */
class ThemeViewModel(application: Application) : BaseViewModel(application) {

    val themeLiveData = ConfigLiveData(Cfg.KEY_APP_THEME) { it.appTheme }

}