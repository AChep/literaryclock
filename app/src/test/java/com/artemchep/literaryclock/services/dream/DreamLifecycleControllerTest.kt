package com.artemchep.literaryclock.services.dream

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import org.junit.Assert.assertEquals
import org.junit.Test

class DreamLifecycleControllerTest {
    private val lifecycleOwner = TestLifecycleOwner()
    private val controller = DreamLifecycleController(
        lifecycleOwner = lifecycleOwner,
        registryFactory = LifecycleRegistry::createUnsafe,
    ).also { controller ->
        lifecycleOwner.delegate = controller.lifecycle
    }

    @Test
    fun normalDreamLifecyclePublishesExpectedEvents() {
        val events = mutableListOf<Event>()
        controller.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            events += event
        })

        controller.onCreate()
        controller.onAttachedToWindow()
        controller.onDreamingStarted()
        controller.onDreamingStopped()
        controller.onDetachedFromWindow()
        controller.onDestroy()

        assertEquals(
            listOf(
                Event.ON_CREATE,
                Event.ON_START,
                Event.ON_RESUME,
                Event.ON_PAUSE,
                Event.ON_STOP,
                Event.ON_DESTROY,
            ),
            events,
        )
        assertEquals(State.DESTROYED, controller.lifecycle.currentState)
    }

    @Test
    fun destroyKeepsLifecycleDestroyedWhenDreamingStopsAfterwards() {
        controller.onCreate()
        controller.onAttachedToWindow()
        controller.onDreamingStarted()

        controller.onDestroy()
        controller.onDreamingStopped()

        assertEquals(State.DESTROYED, controller.lifecycle.currentState)
    }

    @Test
    fun destroyKeepsLifecycleDestroyedWhenWindowDetachesAfterwards() {
        controller.onCreate()
        controller.onAttachedToWindow()
        controller.onDreamingStarted()

        controller.onDestroy()
        controller.onDetachedFromWindow()

        assertEquals(State.DESTROYED, controller.lifecycle.currentState)
    }

    @Test
    fun detachBeforeDreamingStartedDoesNotReplayResume() {
        val events = mutableListOf<Event>()
        controller.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            events += event
        })

        controller.onCreate()
        controller.onAttachedToWindow()
        controller.onDetachedFromWindow()
        controller.onDestroy()

        assertEquals(
            listOf(
                Event.ON_CREATE,
                Event.ON_START,
                Event.ON_STOP,
                Event.ON_DESTROY,
            ),
            events,
        )
    }

    private class TestLifecycleOwner : LifecycleOwner {
        lateinit var delegate: Lifecycle

        override val lifecycle: Lifecycle
            get() = delegate
    }
}
