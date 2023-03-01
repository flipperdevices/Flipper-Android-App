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
        onDispose {
            onEvent(Lifecycle.Event.ON_DESTROY)
            lifecycle.removeObserver(observer)
        }
    }
}
