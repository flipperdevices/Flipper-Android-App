package com.flipperdevices.filemanager.search.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import okio.Path

abstract class SearchDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            path: Path,
            onBack: DecomposeOnBackParameter,
            onFolderSelect: FolderSelectCallback
        ): SearchDecomposeComponent
    }

    fun interface FolderSelectCallback {
        fun invoke(path: Path)
    }
}
