package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinContext
import org.kodein.di.android.kodein
import org.kodein.di.generic.kcontext

/**
 * @author Artem Chepurnoy
 */
abstract class BaseViewModel(application: Application) : AndroidViewModel(application),
    KodeinAware {

    override val kodein: Kodein by kodein(application)

    override val kodeinContext: KodeinContext<Context> = kcontext(application)

}
