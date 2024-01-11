package com.flipperdevices.share.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class KeyReceiveDecomposeComponent : ScreenDecomposeComponent() {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            deeplink: Deeplink.RootLevel.SaveKey,
            onBack: DecomposeOnBackParameter
        ): KeyReceiveDecomposeComponent
    }
}
