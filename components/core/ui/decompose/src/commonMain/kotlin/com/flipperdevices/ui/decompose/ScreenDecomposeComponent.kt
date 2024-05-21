package com.flipperdevices.ui.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.flipperdevices.ui.decompose.util.StatusBarIconStyleProvider
import com.flipperdevices.ui.decompose.util.WindowDecorator
import com.flipperdevices.ui.decompose.util.createWindowDecorator

abstract class ScreenDecomposeComponent(
    componentContext: ComponentContext
) : DecomposeComponent(),
    ComponentContext by componentContext,
    Lifecycle.Callbacks,
    StatusBarIconStyleProvider {
    private val windowDecorator: WindowDecorator by lazy {
        createWindowDecorator(statusBarIconStyleProvider = this)
    }

    init {
        lifecycle.subscribe(this)
    }

    override fun onResume() {
        super.onResume()
        windowDecorator.decorate()
    }

    override fun isStatusBarIconLight(systemIsDark: Boolean) = false
}
