package com.flipperdevices.keyedit.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class KeyEditDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            onSave: (FlipperKey?) -> Unit = { onBack.invoke() },
            flipperKeyPath: FlipperKeyPath,
            title: String?
        ): KeyEditDecomposeComponent

        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            onSave: (FlipperKey?) -> Unit = { onBack.invoke() },
            notSavedFlipperKey: NotSavedFlipperKey,
            title: String?
        ): KeyEditDecomposeComponent
    }
}
