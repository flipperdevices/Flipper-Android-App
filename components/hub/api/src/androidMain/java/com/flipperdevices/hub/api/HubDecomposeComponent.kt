package com.flipperdevices.hub.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

abstract class HubDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {
    abstract fun handleDeeplink(deeplink: Deeplink.BottomBar.HubTab)

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            deeplink: Deeplink.BottomBar.HubTab?,
            onBack: DecomposeOnBackParameter,
        ): HubDecomposeComponent<*>
    }
}
