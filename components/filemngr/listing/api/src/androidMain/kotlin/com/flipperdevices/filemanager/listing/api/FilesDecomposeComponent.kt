package com.flipperdevices.filemanager.listing.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import okio.Path

abstract class FilesDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            path: Path,
            onPathChanged: (Path) -> Unit,
            onUploadClick: () -> Unit
        ): FilesDecomposeComponent
    }
}
