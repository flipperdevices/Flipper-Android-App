package com.flipperdevices.bridge.service.impl.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Subscribe on first emitting {@param lifecycleEvent}
 */
fun LifecycleOwner.subscribeOnFirst(
    lifecycleEvent: Lifecycle.Event,
    listener: () -> Unit
) {
    lateinit var observer: LifecycleObserver
    @Suppress("UnusedPrivateMember")
    observer = LifecycleEventObserver { _, event ->
        if (event == lifecycleEvent) {
            listener()
            lifecycle.removeObserver(observer)
        }
    }

    lifecycle.addObserver(observer)
}
