package com.artemchep.literaryclock.test

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun <T> LiveData<T>.getOrAwaitValue(
    timeout: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {},
): T {
    var observedValue: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            observedValue = value
            latch.countDown()
            removeObserver(this)
        }
    }

    observeForever(observer)
    try {
        afterObserve()

        check(latch.await(timeout, timeUnit)) {
            "LiveData value was never set within $timeout ${timeUnit.name.lowercase()}."
        }
    } finally {
        removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return observedValue as T
}
