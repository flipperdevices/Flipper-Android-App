package com.flipperdevices.filemanager.upload.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.filemanager.upload.api.MultipleFilesPicker
import com.flipperdevices.filemanager.upload.api.UploadDecomposeComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import uk.kulikov.metro.assisted.ContributesAssistedFactory
import okio.Path

@ContributesAssistedFactory(AppGraph::class, UploadDecomposeComponent.Factory::class)
class UploadDecomposeComponentNoop @AssistedInject constructor(
    @Suppress("UnusedPrivateProperty") @Assisted componentContext: ComponentContext,
    @Suppress("UnusedPrivateProperty") @Assisted private val onFilesChanged: (List<ListingItem>) -> Unit,
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
