package com.flipperdevices.bottombar.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

interface BottomBarDecomposeComponent : DecomposeComponent {
    fun handleDeeplink(deeplink: Deeplink.BottomBar)

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            deeplink: Deeplink.BottomBar?
        ): BottomBarDecomposeComponent
    }
}
