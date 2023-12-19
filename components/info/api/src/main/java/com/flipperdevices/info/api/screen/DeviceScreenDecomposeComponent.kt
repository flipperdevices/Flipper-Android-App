package com.flipperdevices.info.api.screen

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeComponent

interface DeviceScreenDecomposeComponent : DecomposeComponent {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
        ): DeviceScreenDecomposeComponent
    }
}
