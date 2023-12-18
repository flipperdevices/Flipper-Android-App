package com.flipperdevices.faphub.fapscreen.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeComponent

interface FapScreenDecomposeComponent : DecomposeComponent {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            id: String
        ): FapScreenDecomposeComponent
    }
}
