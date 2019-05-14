package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.artemchep.literaryclock.Cfg
import com.artemchep.literaryclock.logic.live.ConfigLiveData

/**
 * @author Artem Chepurnoy
 */
class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    val themeLiveData = ConfigLiveData(Cfg.KEY_APP_THEME) { it.appTheme }

}