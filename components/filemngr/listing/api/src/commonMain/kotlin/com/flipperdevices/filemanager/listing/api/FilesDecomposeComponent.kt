package com.flipperdevices.filemanager.listing.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import okio.Path

abstract class FilesDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {
    abstract fun onFileChanged(listingItem: ListingItem)

    fun interface Factory {
        @Suppress("LongParameterList")
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            path: Path,
            pathChangedCallback: PathChangedCallback,
            fileSelectedCallback: FileSelectedCallback,
            moveToCallback: MoveToCallback,
            searchCallback: SearchCallback,
        ): FilesDecomposeComponent
    }

    fun interface PathChangedCallback {
        fun invoke(path: Path)
    }

    fun interface FileSelectedCallback {
        fun invoke(path: Path)
    }

    fun interface SearchCallback {
        fun invoke()
    }

    fun interface UploadCallback {
        fun invoke()
    }
    fun interface MoveToCallback {
        fun invoke(fullPaths: List<Path>)
    }
}
