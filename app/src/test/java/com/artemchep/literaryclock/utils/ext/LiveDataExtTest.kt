package com.artemchep.literaryclock.utils.ext

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test

class LiveDataExtTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun observeOnceRemovesObserverWhenCallbackThrows() {
        val liveData = MutableLiveData(1)

        assertThrows(IllegalStateException::class.java) {
            liveData.observeOnce<Int> {
                throw IllegalStateException("Callback failed")
            }
        }

        assertThat(liveData.hasObservers()).isFalse()
    }
}
