package com.flipperdevices.wearable.emulate.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class WearEmulateDecomposeComponent(componentContext: ComponentContext) : ScreenDecomposeComponent(
    componentContext
) {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            flipperKeyPath: FlipperKeyPath,
            onBack: DecomposeOnBackParameter
        ): WearEmulateDecomposeComponent
    }
}
