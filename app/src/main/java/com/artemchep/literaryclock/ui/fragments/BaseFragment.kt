package com.artemchep.literaryclock.ui.fragments

import androidx.fragment.app.Fragment
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein

/**
 * @author Artem Chepurnoy
 */
abstract class BaseFragment : Fragment(), KodeinAware {
    override val kodein: Kodein by closestKodein()
}
