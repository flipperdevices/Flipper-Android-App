package com.flipperdevices.filemanager.upload.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.filemanager.upload.api.MultipleFilesPicker
import com.flipperdevices.filemanager.upload.api.UploadDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory
import okio.Path

@ContributesAssistedFactory(AppGraph::class, UploadDecomposeComponent.Factory::class)
class UploadDecomposeComponentNoop @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onFilesChanged: (List<ListingItem>) -> Unit,
) : UploadDecomposeComponent {
    @Composable
    override fun rememberMultipleFilesPicker(
        path: Path
    ) = MultipleFilesPicker {}

    @Composable
    override fun Render() {
        // Empty
    }
}