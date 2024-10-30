package com.flipperdevices.filemanager.listing.impl.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.preference.pb.FileManagerOrientation
import com.flipperdevices.filemanager.listing.impl.composable.appbar.FileListAppBar
import com.flipperdevices.filemanager.listing.impl.composable.dialog.CreateFileDialogComposable
import com.flipperdevices.filemanager.listing.impl.composable.dialog.DeleteFileDialog
import com.flipperdevices.filemanager.listing.impl.composable.options.FullScreenBottomBarOptions
import com.flipperdevices.filemanager.listing.impl.model.PathWithType
import com.flipperdevices.filemanager.listing.impl.viewmodel.DeleteFilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.EditFileViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.FilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.OptionsViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.SelectionViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.StorageInfoViewModel
import okio.Path

@Suppress("LongMethod")
@Composable
fun ComposableFileListScreen(
    path: Path,
    editFileViewModel: EditFileViewModel,
    deleteFileViewModel: DeleteFilesViewModel,
    filesViewModel: FilesViewModel,
    optionsViewModel: OptionsViewModel,
    storageInfoViewModel: StorageInfoViewModel,
    selectionViewModel: SelectionViewModel,
    onBack: () -> Unit,
    onUploadClick: () -> Unit,
    onSearchClick: () -> Unit,
    onPathChange: (Path) -> Unit,
    onEditFileClick: (Path) -> Unit,
    onFileMoreClick: (PathWithType) -> Unit,
    modifier: Modifier = Modifier
) {
    val canCreateFiles by editFileViewModel.canCreateFiles.collectAsState()
    val canDeleteFiles by deleteFileViewModel.canDeleteFiles.collectAsState()
    val filesListState by filesViewModel.state.collectAsState()
    val optionsState by optionsViewModel.state.collectAsState()
    val deleteFileState by deleteFileViewModel.state.collectAsState()
    val selectionState by selectionViewModel.state.collectAsState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            FileListAppBar(
                path = path,
                selectionState = selectionState,
                selectionViewModel = selectionViewModel,
                filesListState = filesListState,
                optionsState = optionsState,
                optionsViewModel = optionsViewModel,
                canCreateFiles = canCreateFiles,
                onUploadClick = onUploadClick,
                editFileViewModel = editFileViewModel,
                onBack = onBack,
                onSearchClick = onSearchClick
            )
        }
    ) { contentPadding ->
        CreateFileDialogComposable(
            editFileViewModel = editFileViewModel,
        )
        DeleteFileDialog(
            deleteFileState = deleteFileState,
            deleteFileViewModel = deleteFileViewModel
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
                        onDelete = deleteFileViewModel::tryDelete,
                        selectionState = selectionState,
                        onCheckToggle = selectionViewModel::togglePath,
                        onFileMoreClick = onFileMoreClick,
                        onEditFileClick = onEditFileClick
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
        FilesFailComposable(
            filesListState = filesListState,
            onPathChange = onPathChange,
            path = path,
            onUploadClick = onUploadClick
        )
        FullScreenBottomBarOptions(
            modifier = Modifier.padding(contentPadding),
            selectionState = selectionState,
            filesListState = filesListState,
            selectionViewModel = selectionViewModel,
            editFileViewModel = editFileViewModel,
            deleteFileViewModel = deleteFileViewModel,
        )
    }
}
