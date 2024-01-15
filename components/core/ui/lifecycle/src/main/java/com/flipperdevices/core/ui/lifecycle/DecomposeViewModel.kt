package com.flipperdevices.core.ui.lifecycle

import androidx.annotation.CallSuper
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import kotlinx.coroutines.SupervisorJob

abstract class DecomposeViewModel : InstanceKeeper.Instance, LifecycleOwner {
    private val registry by lazy { LifecycleRegistry(Lifecycle.State.INITIALIZED) }
    override val lifecycle = registry

    protected val viewModelScope = coroutineScope(SupervisorJob() + FlipperDispatchers.getDefault())

    init {
        registry.onCreate()
        registry.onStart()
        registry.onResume()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        registry.onPause()
        registry.onStop()
        registry.onDestroy()
    }
}
