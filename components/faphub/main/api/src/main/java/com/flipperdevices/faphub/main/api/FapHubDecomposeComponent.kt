package com.flipperdevices.faphub.main.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeComponent

interface FapHubDecomposeComponent : DecomposeComponent {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): FapHubDecomposeComponent
    }
}
