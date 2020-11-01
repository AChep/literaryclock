package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI

/**
 * @author Artem Chepurnoy
 */
abstract class BaseViewModel(application: Application) : AndroidViewModel(application),
    DIAware {

    override val di by closestDI(application)

}
