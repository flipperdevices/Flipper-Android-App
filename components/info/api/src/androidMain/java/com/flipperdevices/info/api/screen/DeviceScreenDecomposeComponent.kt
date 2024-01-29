package com.flipperdevices.info.api.screen

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

abstract class DeviceScreenDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {
    abstract fun handleDeeplink(deeplink: Deeplink.BottomBar.DeviceTab)
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            deeplink: Deeplink.BottomBar.DeviceTab?,
            onBack: DecomposeOnBackParameter
        ): DeviceScreenDecomposeComponent<*>
    }
}
