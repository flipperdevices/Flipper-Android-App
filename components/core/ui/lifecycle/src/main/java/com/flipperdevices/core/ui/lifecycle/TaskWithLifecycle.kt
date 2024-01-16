package com.flipperdevices.core.ui.lifecycle

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class TaskWithLifecycle : LifecycleOwner {
    private val registry by lazy { LifecycleRegistry(Lifecycle.State.INITIALIZED) }

    suspend fun onStart() = withContext(Dispatchers.Main) {
        registry.onCreate()
        registry.onStart()
        registry.onResume()
    }

    override val lifecycle: Lifecycle
        get() = registry

    suspend fun onStop() = withContext(Dispatchers.Main) {
        if (registry.state == Lifecycle.State.DESTROYED) {
            return@withContext
        }
        registry.onPause()
        registry.onStop()
        registry.onDestroy()
    }
}
