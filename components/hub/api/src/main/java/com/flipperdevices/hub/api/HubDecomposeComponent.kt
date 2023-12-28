package com.flipperdevices.hub.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.ui.decompose.DecomposeComponent

interface HubDecomposeComponent : DecomposeComponent {
    fun handleDeeplink(deeplink: Deeplink.BottomBar.HubTab)
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            deeplink: Deeplink.BottomBar.HubTab?
        ): HubDecomposeComponent
    }
}
