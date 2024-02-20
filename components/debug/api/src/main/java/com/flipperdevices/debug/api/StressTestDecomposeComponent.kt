package com.flipperdevices.debug.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class StressTestDecomposeComponent(componentContext: ComponentContext) : ScreenDecomposeComponent(
    componentContext
) {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): StressTestDecomposeComponent
    }
}
