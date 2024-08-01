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
            onIfrFileFound: (ifrFileId: Long) -> Unit
        ): SetupScreenDecomposeComponent
    }

    class Param(
        val brandId: Long,
        val categoryId: Long,
    )
}
