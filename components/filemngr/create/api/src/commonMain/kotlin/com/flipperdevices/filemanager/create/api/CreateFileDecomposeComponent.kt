package com.flipperdevices.filemanager.create.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import kotlinx.coroutines.flow.StateFlow
import okio.Path

abstract class CreateFileDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {
    abstract val canCreateFiles: StateFlow<Boolean>

    abstract fun startCreateFile(parent: Path)

    abstract fun startCreateFolder(parent: Path)

    abstract fun startCreate(parent: Path, type: FileType)

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            createCallback: CreatedCallback,
        ): CreateFileDecomposeComponent
    }

    fun interface CreatedCallback {
        fun invoke(item: ListingItem)
    }
}
