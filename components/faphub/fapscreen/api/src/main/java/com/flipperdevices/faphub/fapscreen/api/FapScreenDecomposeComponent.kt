package com.flipperdevices.faphub.fapscreen.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

abstract class FapScreenDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            id: String,
            onBack: DecomposeOnBackParameter
        ): FapScreenDecomposeComponent<*>
    }
}
