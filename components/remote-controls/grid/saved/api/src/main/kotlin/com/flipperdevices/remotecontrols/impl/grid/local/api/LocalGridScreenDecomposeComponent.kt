package com.flipperdevices.remotecontrols.impl.grid.local.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.remotecontrols.api.model.GridControlParam
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class LocalGridScreenDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            param: GridControlParam.Path,
            onBack: DecomposeOnBackParameter,
            onUiNotFound: () -> Unit
        ): LocalGridScreenDecomposeComponent
    }
}
