package com.flipperdevices.info.api.screen

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.ui.decompose.DecomposeComponent

interface DeviceScreenDecomposeComponent : DecomposeComponent {
    fun handleDeeplink(deeplink: Deeplink.BottomBar.DeviceTab)
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            deeplink: Deeplink.BottomBar.DeviceTab?
        ): DeviceScreenDecomposeComponent
    }
}
