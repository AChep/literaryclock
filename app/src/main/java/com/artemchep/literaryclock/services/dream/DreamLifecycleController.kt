package com.artemchep.literaryclock.services.dream

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

internal class DreamLifecycleController(
    lifecycleOwner: LifecycleOwner,
    registryFactory: (LifecycleOwner) -> LifecycleRegistry = ::LifecycleRegistry,
) {
    private val lifecycleRegistry = registryFactory(lifecycleOwner)

    private var created = false
    private var attached = false
    private var dreaming = false
    private var destroyed = false

    val lifecycle: Lifecycle
        get() = lifecycleRegistry

    fun onCreate() {
        created = true
        sync()
    }

    fun onAttachedToWindow() {
        attached = true
        sync()
    }

    fun onDreamingStarted() {
        dreaming = true
        sync()
    }

    fun onDreamingStopped() {
        dreaming = false
        sync()
    }

    fun onDetachedFromWindow() {
        attached = false
        dreaming = false
        sync()
    }

    fun onDestroy() {
        created = true
        attached = false
        dreaming = false
        destroyed = true
        sync()
    }

    private fun sync() {
        lifecycleRegistry.currentState = when {
            destroyed -> Lifecycle.State.DESTROYED
            dreaming -> Lifecycle.State.RESUMED
            attached -> Lifecycle.State.STARTED
            created -> Lifecycle.State.CREATED
            else -> Lifecycle.State.INITIALIZED
        }
    }
}
