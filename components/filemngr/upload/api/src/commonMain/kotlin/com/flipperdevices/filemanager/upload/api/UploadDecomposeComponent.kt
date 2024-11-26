package com.flipperdevices.filemanager.upload.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import okio.Path

interface UploadDecomposeComponent {

    @Composable
    fun rememberMultipleFilesPicker(path: Path): MultipleFilesPicker

    @Composable
    fun Render()

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onFilesChanged: (List<ListingItem>) -> Unit,
        ): UploadDecomposeComponent
    }
}
