package com.flipperdevices.infrared.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class InfraredEditorDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            keyPath: FlipperKeyPath,
            onBack: DecomposeOnBackParameter
        ): InfraredEditorDecomposeComponent
    }
}
