package com.flipperdevices.filemanager.rename.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import okio.Path

abstract class RenameDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {

    abstract fun startRename(fullPath: Path, type: FileType)

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            renamedCallback: RenamedCallback,
        ): RenameDecomposeComponent
    }

    fun interface RenamedCallback {
        fun invoke(oldFullPath: Path, newFullPath: Path)
    }
}
