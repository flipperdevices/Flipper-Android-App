package com.flipperdevices.faphub.screenshotspreview.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.faphub.screenshotspreview.api.model.ScreenshotsPreviewParam
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class ScreenshotsPreviewDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            param: ScreenshotsPreviewParam,
            onBack: DecomposeOnBackParameter,
        ): ScreenshotsPreviewDecomposeComponent
    }
}
