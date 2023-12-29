package com.flipperdevices.share.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

interface KeyReceiveDecomposeComponent : DecomposeComponent {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            deeplink: Deeplink.RootLevel.SaveKey,
            onBack: DecomposeOnBackParameter
        ): KeyReceiveDecomposeComponent
    }
}
