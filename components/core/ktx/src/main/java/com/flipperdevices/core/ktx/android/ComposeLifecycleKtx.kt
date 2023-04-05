package com.flipperdevices.core.ktx.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun OnLifecycleEvent(onEvent: (event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    var currentEvent by remember { mutableStateOf<Lifecycle.Event?>(null) }

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            if (currentEvent != event) {
                eventHandler.value(event)
                currentEvent = event
            }
        }

        lifecycle.addObserver(observer)
        emitCurrentState(lifecycle.currentState, onEvent)
        onDispose {
            emitOnDestroy(currentEvent, onEvent)
            lifecycle.removeObserver(observer)
        }
    }
}

private fun emitCurrentState(
    currentState: Lifecycle.State,
    onEvent: (event: Lifecycle.Event) -> Unit
) {
    val event = when (currentState) {
        Lifecycle.State.DESTROYED -> Lifecycle.Event.ON_DESTROY
        Lifecycle.State.INITIALIZED -> Lifecycle.Event.ON_CREATE
        Lifecycle.State.CREATED -> Lifecycle.Event.ON_CREATE
        Lifecycle.State.STARTED -> Lifecycle.Event.ON_START
        Lifecycle.State.RESUMED -> Lifecycle.Event.ON_RESUME
    }
    onEvent(event)
}

/**
 * If we left the screen, but all states have not passed - we must process all changes
 */
private fun emitOnDestroy(
    currentEvent: Lifecycle.Event?,
    onEvent: (event: Lifecycle.Event) -> Unit
) {
    val newState = when (currentEvent) {
        Lifecycle.Event.ON_CREATE,
        Lifecycle.Event.ON_START,
        Lifecycle.Event.ON_RESUME -> Lifecycle.Event.ON_PAUSE
        Lifecycle.Event.ON_PAUSE -> Lifecycle.Event.ON_STOP
        Lifecycle.Event.ON_STOP -> Lifecycle.Event.ON_DESTROY
        Lifecycle.Event.ON_DESTROY -> null
        Lifecycle.Event.ON_ANY,
        null -> Lifecycle.Event.ON_PAUSE
    } ?: return

    onEvent(newState)
    emitOnDestroy(newState, onEvent)
}
