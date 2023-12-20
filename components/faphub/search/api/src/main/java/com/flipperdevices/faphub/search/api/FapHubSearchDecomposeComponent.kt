package com.flipperdevices.faphub.search.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeComponent

interface FapHubSearchDecomposeComponent : DecomposeComponent {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): FapHubSearchDecomposeComponent
    }
}
