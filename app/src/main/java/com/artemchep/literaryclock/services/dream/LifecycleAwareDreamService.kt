package com.artemchep.literaryclock.services.dream

import android.service.dreams.DreamService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

/**
 * @author Artem Chepurnoy
 */
abstract class LifecycleAwareDreamService : DreamService() {

    val lifecycleOwner: LifecycleOwner = object : LifecycleOwner {
        override val lifecycle: Lifecycle
            get() = lifecycleController.lifecycle
    }

    private val lifecycleController by lazy { DreamLifecycleController(lifecycleOwner) }

    override fun onCreate() {
        super.onCreate()
        lifecycleController.onCreate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifecycleController.onAttachedToWindow()
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        lifecycleController.onDreamingStarted()
    }

    override fun onDreamingStopped() {
        lifecycleController.onDreamingStopped()
        super.onDreamingStopped()
    }

    override fun onDetachedFromWindow() {
        lifecycleController.onDetachedFromWindow()
        super.onDetachedFromWindow()
    }

    override fun onDestroy() {
        lifecycleController.onDestroy()
        super.onDestroy()
    }

}
