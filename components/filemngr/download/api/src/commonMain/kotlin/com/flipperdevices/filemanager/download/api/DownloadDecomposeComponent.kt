package com.flipperdevices.filemanager.download.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.filemanager.download.model.DownloadableFile
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import kotlinx.coroutines.flow.StateFlow

abstract class DownloadDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {
    abstract val isInProgress: StateFlow<Boolean>

    abstract fun onCancel()

    abstract fun download(file: DownloadableFile)

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
        ): DownloadDecomposeComponent
    }
}
