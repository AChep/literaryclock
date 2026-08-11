package com.artemchep.config.common

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.artemchep.config.Config
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SharedPrefConfigTest {
    private val context = ApplicationProvider.getApplicationContext<Application>()

    @Test
    fun successfulEditNotifiesChangedKeys() {
        val config = TestConfig(uniqueName("successful_edit"))
        val changedKeys = mutableListOf<Set<String>>()
        val listener = object : Config.OnConfigChangedListener<String> {
            override fun onConfigChanged(keys: Set<String>) {
                changedKeys += keys
            }
        }

        config.init(context)
        config.observe(listener)

        config.edit(context) {
            config.enabled = false
        }

        assertThat(config.enabled).isFalse()
        assertThat(changedKeys).containsExactly(setOf("enabled"))
    }

    @Test
    fun failedEditDoesNotNotifyChangedKeys() {
        val config = TestConfig(uniqueName("failed_edit"))
        val changedKeys = mutableListOf<Set<String>>()
        val listener = object : Config.OnConfigChangedListener<String> {
            override fun onConfigChanged(keys: Set<String>) {
                changedKeys += keys
            }
        }

        config.init(context)
        config.observe(listener)

        try {
            config.edit(context) {
                config.enabled = false
                error("boom")
            }
        } catch (e: IllegalStateException) {
            assertThat(e).hasMessageThat().isEqualTo("boom")
        }

        assertThat(config.enabled).isTrue()
        assertThat(changedKeys).isEmpty()
    }

    @Test
    fun unchangedEditDoesNotNotifyChangedKeys() {
        val config = TestConfig(uniqueName("unchanged_edit"))
        val changedKeys = mutableListOf<Set<String>>()
        val listener = object : Config.OnConfigChangedListener<String> {
            override fun onConfigChanged(keys: Set<String>) {
                changedKeys += keys
            }
        }

        config.init(context)
        config.edit(context) {
            config.enabled = false
        }
        config.observe(listener)

        config.edit(context) {
            config.enabled = false
        }

        assertThat(config.enabled).isFalse()
        assertThat(changedKeys).isEmpty()
    }

    private class TestConfig(name: String) : SharedPrefConfig(name) {
        var enabled by configDelegate("enabled", true)
    }

    private fun uniqueName(suffix: String) = "test_config_${suffix}_${System.nanoTime()}"
}
