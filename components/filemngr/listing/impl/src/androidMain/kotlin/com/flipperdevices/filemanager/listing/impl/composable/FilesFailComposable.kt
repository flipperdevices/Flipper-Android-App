package com.flipperdevices.filemanager.listing.impl.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.filemanager.listing.impl.viewmodel.FilesViewModel
import okio.Path

@Composable
fun FilesFailComposable(
    filesListState: FilesViewModel.State,
    onPathChange: (Path) -> Unit,
    onUploadClick: () -> Unit,
    path: Path,
    modifier: Modifier = Modifier
) {
    when (val localFilesListState = filesListState) {
        FilesViewModel.State.CouldNotListPath -> {
            ListingErrorComposable(
                path = path,
                onPathChange = onPathChange,
                modifier = modifier.fillMaxSize()
            )
        }

        is FilesViewModel.State.Loaded -> {
            if (localFilesListState.files.isEmpty()) {
                NoFilesComposable(
                    onUploadFilesClick = onUploadClick,
                    modifier = modifier.fillMaxSize()
                )
            }
        }

        else -> Unit
    }
}
