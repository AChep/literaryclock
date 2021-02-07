package com.artemchep.literaryclock.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI

/**
 * @author Artem Chepurnoy
 */
abstract class BaseFragment<Binding : ViewBinding> : Fragment(), DIAware {
    override val di by closestDI()

    protected abstract val viewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> Binding

    private var _viewBinding: Binding? = null

    @Suppress("MemberVisibilityCanBePrivate")
    protected val viewBinding: Binding
        get() = _viewBinding
            ?: error("You can not access views after #onDestroyView")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = viewBindingFactory(onCreateViewInflater(inflater), container, false)
        .also(::_viewBinding::set)
        .root

    protected open fun onCreateViewInflater(inflater: LayoutInflater): LayoutInflater = inflater

    override fun onDestroyView() {
        _viewBinding = null
        super.onDestroyView()
    }
}
