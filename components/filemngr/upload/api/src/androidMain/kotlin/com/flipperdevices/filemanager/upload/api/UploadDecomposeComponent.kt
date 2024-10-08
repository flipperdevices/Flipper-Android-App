package com.flipperdevices.filemanager.upload.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import okio.Path

abstract class UploadDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            path: Path,
            onFinish: () -> Unit
        ): UploadDecomposeComponent
    }
}
