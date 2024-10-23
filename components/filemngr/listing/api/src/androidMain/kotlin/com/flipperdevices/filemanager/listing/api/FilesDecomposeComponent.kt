package com.flipperdevices.filemanager.listing.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import okio.Path

abstract class FilesDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {
    fun interface Factory {
        @Suppress("LongParameterList")
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            path: Path,
            onPathChanged: (Path) -> Unit,
            searchCallback: SearchCallback,
            uploadCallback: UploadCallback
        ): FilesDecomposeComponent
    }

    fun interface SearchCallback {
        fun invoke()
    }

    fun interface UploadCallback {
        fun invoke()
    }
}
