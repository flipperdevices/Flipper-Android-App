package com.flipperdevices.remotecontrols.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class CreateControlDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            savedKey: FlipperKeyPath,
            originalKey: NotSavedFlipperKey,
            onBack: DecomposeOnBackParameter
        ): CreateControlDecomposeComponent
    }
}
