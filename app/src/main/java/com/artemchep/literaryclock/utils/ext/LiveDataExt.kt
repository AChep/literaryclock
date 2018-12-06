package com.artemchep.literaryclock.utils.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Registers an observer for one invoke and then
 * unregisters it.
 */
inline fun <T> LiveData<T>.observeOnce(crossinline observer: (T) -> Unit) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T) {
            observer.invoke(t)
            removeObserver(this)
        }
    })
}
