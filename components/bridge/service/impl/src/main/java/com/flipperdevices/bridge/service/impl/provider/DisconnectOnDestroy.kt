package com.flipperdevices.bridge.service.impl.provider

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 * Subscribe on first emitting {@param lifecycleEvent}
 */
fun LifecycleOwner.subscribeOnFirst(
    lifecycleEvent: Lifecycle.Event,
    listener: () -> Unit
) {
    lateinit var observer: LifecycleObserver
    observer = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
        fun onEvent(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == lifecycleEvent) {
                listener()
                lifecycle.removeObserver(observer)
            }
        }
    }

    lifecycle.addObserver(observer)
}
