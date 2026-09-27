package com.artemchep.literaryclock.logic

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A lifecycle-aware observable that sends only new updates after subscription, used for events like
 * navigation and Snackbar messages.
 *
 *
 * This avoids a common problem with events: on configuration change (like rotation) an update
 * can be emitted if the observer is active. This LiveData only calls the observable if there's an
 * explicit call to setValue() or call().
 *
 *
 * Note that only one observer is going to be notified of changes.
 */
class SingleLiveEvent<T> : MutableLiveData<T>() {

    companion object {
        private const val TAG = "SingleLiveEvent"
    }

    private val pending = AtomicBoolean(false)

    // Match by equality, as LiveData does. A callback's hash code can change
    // when its bound receiver is mutable.
    private val observers = mutableListOf<Pair<Observer<in T>, Observer<T>>>()

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }

        // Observe the internal MutableLiveData
        super.observe(owner, wrapObserver(observer))
    }

    override fun observeForever(observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }

        super.observeForever(wrapObserver(observer))
    }

    override fun removeObserver(observer: Observer<in T>) {
        val index = observers.indexOfFirst { (original, wrapped) ->
            original == observer || wrapped == observer
        }
        val wrappedObserver: Observer<in T> = if (index >= 0) {
            observers.removeAt(index).second
        } else {
            observer
        }
        super.removeObserver(wrappedObserver)
    }

    @MainThread
    override fun setValue(t: T?) {
        pending.set(true)
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    private fun wrapObserver(observer: Observer<in T>): Observer<T> =
        observers.firstOrNull { it.first == observer }?.second
            ?: Observer<T> { t ->
                if (pending.compareAndSet(true, false)) {
                    observer.onChanged(t)
                }
            }.also { wrapped ->
                observers.add(observer to wrapped)
            }

}
