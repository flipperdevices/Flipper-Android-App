package com.flipperdevices.keyedit.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class KeyEditDecomposeComponent : ScreenDecomposeComponent() {
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            flipperKeyPath: FlipperKeyPath,
            title: String?
        ): KeyEditDecomposeComponent

        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            notSavedFlipperKey: NotSavedFlipperKey,
            title: String?
        ): KeyEditDecomposeComponent
    }
}
