package com.flipperdevices.bridge.connection.screens.search

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class ConnectionSearchDecomposeComponent(
    componentContext: ComponentContext,
) : ScreenDecomposeComponent(componentContext) {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): ConnectionSearchDecomposeComponent
    }
}
