package com.flipperdevices.bridge.service.impl.provider

import android.annotation.SuppressLint
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.LifecycleRegistry

public class TestLifecycleOwner(
    initialState: Lifecycle.State = Lifecycle.State.STARTED
) : LifecycleOwner {
    // it is in test artifact
    @SuppressLint("VisibleForTests")
    private val lifecycleRegistry = LifecycleRegistry(initialState)

    override val lifecycle = lifecycleRegistry
}
