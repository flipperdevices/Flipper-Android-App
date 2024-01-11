package com.flipperdevices.shake2report.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class Shake2ReportDecomposeComponent : ScreenDecomposeComponent() {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter
        ): Shake2ReportDecomposeComponent
    }
}
