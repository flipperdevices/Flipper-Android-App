package com.flipperdevices.remotecontrols.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class SetupScreenDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            param: Param,
            onBack: () -> Unit,
            onIrFileReady: (id: Long) -> Unit
        ): SetupScreenDecomposeComponent
    }

    class Param(
        val brandId: Long,
        val categoryId: Long,
    )
}
