package com.artemchep.literaryclock.logic

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SingleLiveEventTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun observeForeverDoesNotReceiveConsumedEvent() {
        val liveEvent = SingleLiveEvent<String>()
        val lifecycleOwner = TestLifecycleOwner()
        val consumedValues = mutableListOf<String>()
        val values = mutableListOf<String>()

        liveEvent.observe(lifecycleOwner, consumedValues::add)
        liveEvent.value = "old"
        lifecycleOwner.destroy()
        liveEvent.observeForever(values::add)
        liveEvent.value = "new"

        assertThat(consumedValues).containsExactly("old")
        assertThat(values).containsExactly("new")
    }

    @Test
    fun removeObserverRemovesOriginalForeverObserver() {
        val liveEvent = SingleLiveEvent<String>()
        val values = mutableListOf<String>()
        val observer = Observer<String>(values::add)

        liveEvent.observeForever(observer)
        liveEvent.removeObserver(observer)
        liveEvent.value = "new"

        assertThat(values).isEmpty()
    }

    private class TestLifecycleOwner : LifecycleOwner {
        private val registry = LifecycleRegistry.createUnsafe(this).apply {
            currentState = Lifecycle.State.STARTED
        }

        override val lifecycle: Lifecycle
            get() = registry

        fun destroy() {
            registry.currentState = Lifecycle.State.DESTROYED
        }
    }
}
