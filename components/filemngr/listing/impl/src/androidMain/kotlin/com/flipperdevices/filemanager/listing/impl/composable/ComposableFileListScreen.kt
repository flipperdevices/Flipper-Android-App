package com.flipperdevices.filemanager.listing.impl.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.preference.pb.FileManagerOrientation
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.filemanager.listing.impl.composable.dialog.CreateFileDialogComposable
import com.flipperdevices.filemanager.listing.impl.composable.dialog.DeleteFileDialog
import com.flipperdevices.filemanager.listing.impl.viewmodel.CreateFileViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.DeleteFilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.FilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.OptionsViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.StorageInfoViewModel
import okio.Path

@Suppress("LongMethod")
@Composable
fun ComposableFileListScreen(
    path: Path,
    createFileViewModel: CreateFileViewModel,
    deleteFileViewModel: DeleteFilesViewModel,
    filesViewModel: FilesViewModel,
    optionsViewModel: OptionsViewModel,
    storageInfoViewModel: StorageInfoViewModel,
    onBack: () -> Unit,
    onUploadClick: () -> Unit,
    onPathChange: (Path) -> Unit,
    modifier: Modifier = Modifier
) {
    val canCreateFiles by createFileViewModel.canCreateFiles.collectAsState()
    val canDeleteFiles by deleteFileViewModel.canDeleteFiles.collectAsState()
    val filesListState by filesViewModel.state.collectAsState()
    val optionsState by optionsViewModel.state.collectAsState()
    val deleteFileState by deleteFileViewModel.state.collectAsState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            OrangeAppBar(
                title = "File Manager",
                endBlock = {
                    MoreIconComposable(
                        optionsState = optionsState,
                        onAction = optionsViewModel::onAction,
                        canCreateFiles = canCreateFiles,
                        onUploadClick = onUploadClick,
                        onSelectClick = {},
                        onCreateFolderClick = createFileViewModel::onCreateFolderClick,
                        onCreateFileClick = createFileViewModel::onCreateFileClick
                    )
                },
                onBack = onBack::invoke,
            )
        }
    ) { contentPadding ->
        CreateFileDialogComposable(
            createFileViewModel = createFileViewModel,
            path = path
        )
        DeleteFileDialog(
            deleteFileState = deleteFileState,
            deleteFileViewModel = deleteFileViewModel
        )
        FilesFailComposable(
            filesListState = filesListState,
            onPathChange = onPathChange,
            path = path
        )
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(14.dp),
            columns = when (optionsState.orientation) {
                FileManagerOrientation.GRID -> GridCells.Fixed(2)
                is FileManagerOrientation.Unrecognized,
                FileManagerOrientation.LIST -> GridCells.Fixed(1)
            }
        ) {
            FilesSpanComposable(
                path = path,
                storageInfoViewModel = storageInfoViewModel,
                onPathChange = onPathChange
            )

            when (val localFilesListState = filesListState) {
                is FilesViewModel.State.Loaded -> {
                    LoadedFilesComposable(
                        path = path,
                        deleteFileState = deleteFileState,
                        filesState = localFilesListState,
                        orientation = optionsState.orientation,
                        canDeleteFiles = canDeleteFiles,
                        onPathChanged = onPathChange,
                        onDelete = deleteFileViewModel::tryDelete
                    )
                }

                FilesViewModel.State.Loading -> {
                    FilesPlaceholderComposable(optionsState.orientation)
                }

                FilesViewModel.State.Unsupported -> {
                    item { NoListingFeatureComposable() }
                }

                FilesViewModel.State.CouldNotListPath -> Unit
            }
        }
    }
}
