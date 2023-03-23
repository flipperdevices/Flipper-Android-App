package com.flipperdevices.core.ui.lifecycle

import android.app.Application
import androidx.annotation.CallSuper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

abstract class AndroidLifecycleViewModel(
    application: Application
) : AndroidViewModel(application), LifecycleOwner {
    private val registry by lazy { LifecycleRegistry(this) }

    init {
        registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        registry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override val lifecycle: Lifecycle
        get() = registry

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        registry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        registry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}
