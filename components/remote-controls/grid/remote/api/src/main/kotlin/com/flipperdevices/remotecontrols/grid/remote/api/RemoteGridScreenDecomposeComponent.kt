package com.flipperdevices.remotecontrols.grid.remote.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.remotecontrols.api.model.ServerRemoteControlParam
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class RemoteGridScreenDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            param: ServerRemoteControlParam,
            onBack: DecomposeOnBackParameter,
            onSaveKey: (NotSavedFlipperKey) -> Unit
        ): RemoteGridScreenDecomposeComponent
    }
}
