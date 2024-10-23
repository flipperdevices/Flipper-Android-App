package com.flipperdevices.remotecontrols.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class InfraredsScreenDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            brandId: Long,
            onBack: () -> Unit,
            onRemoteFound: (Long, String) -> Unit
        ): InfraredsScreenDecomposeComponent
    }
}
