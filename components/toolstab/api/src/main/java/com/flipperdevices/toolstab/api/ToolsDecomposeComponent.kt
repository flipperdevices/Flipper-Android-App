package com.flipperdevices.toolstab.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

abstract class ToolsDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {
    abstract fun handleDeeplink(deeplink: Deeplink.BottomBar.ToolsTab)

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            deeplink: Deeplink.BottomBar.ToolsTab?,
            onBack: DecomposeOnBackParameter,
            onDeeplink: (Deeplink.BottomBar) -> Unit
        ): ToolsDecomposeComponent<*>
    }
}
