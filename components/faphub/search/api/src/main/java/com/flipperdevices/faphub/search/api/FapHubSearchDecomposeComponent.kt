package com.flipperdevices.faphub.search.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

interface FapHubSearchDecomposeComponent : DecomposeComponent {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter
        ): FapHubSearchDecomposeComponent
    }
}
