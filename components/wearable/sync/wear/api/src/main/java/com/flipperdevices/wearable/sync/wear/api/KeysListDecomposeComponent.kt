package com.flipperdevices.wearable.sync.wear.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.wearrootscreen.model.WearRootConfig

interface KeysListDecomposeComponent : DecomposeComponent {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            navigation: StackNavigation<WearRootConfig>
        ): KeysListDecomposeComponent
    }
}
