package com.artemchep.literaryclock.ui.fragments

import androidx.fragment.app.Fragment
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI

/**
 * @author Artem Chepurnoy
 */
abstract class BaseFragment : Fragment(), DIAware {
    override val di by closestDI()
}
