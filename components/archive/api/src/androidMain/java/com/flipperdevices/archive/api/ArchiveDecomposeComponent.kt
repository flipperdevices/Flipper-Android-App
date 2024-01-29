package com.flipperdevices.archive.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

abstract class ArchiveDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {
    abstract fun handleDeeplink(deeplink: Deeplink.BottomBar.ArchiveTab)
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            deeplink: Deeplink.BottomBar.ArchiveTab?,
            onBack: DecomposeOnBackParameter
        ): ArchiveDecomposeComponent<*>
    }
}
