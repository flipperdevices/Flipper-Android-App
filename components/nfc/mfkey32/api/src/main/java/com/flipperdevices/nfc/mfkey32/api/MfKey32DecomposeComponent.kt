package com.flipperdevices.nfc.mfkey32.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class MfKey32DecomposeComponent(componentContext: ComponentContext) : ScreenDecomposeComponent(
    componentContext
) {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter
        ): MfKey32DecomposeComponent
    }
}
