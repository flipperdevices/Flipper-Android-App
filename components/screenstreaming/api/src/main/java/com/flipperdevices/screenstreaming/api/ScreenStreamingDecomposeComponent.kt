package com.flipperdevices.screenstreaming.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class ScreenStreamingDecomposeComponent : ScreenDecomposeComponent() {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter
        ): ScreenStreamingDecomposeComponent
    }
}
