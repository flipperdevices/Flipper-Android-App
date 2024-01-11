package com.flipperdevices.wearable.sync.wear.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import com.flipperdevices.wearrootscreen.model.WearRootConfig

abstract class KeysListDecomposeComponent(componentContext: ComponentContext) : ScreenDecomposeComponent(
    componentContext
) {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            navigation: StackNavigation<WearRootConfig>
        ): KeysListDecomposeComponent
    }
}
