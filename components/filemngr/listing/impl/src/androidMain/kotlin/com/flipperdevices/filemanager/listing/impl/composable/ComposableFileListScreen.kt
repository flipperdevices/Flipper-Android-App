package com.flipperdevices.filemanager.listing.impl.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.core.preference.pb.FileManagerOrientation
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.filemanager.listing.impl.composable.appbar.CloseSelectionAppBar
import com.flipperdevices.filemanager.listing.impl.composable.dialog.CreateFileDialogComposable
import com.flipperdevices.filemanager.listing.impl.composable.dialog.DeleteFileDialog
import com.flipperdevices.filemanager.listing.impl.viewmodel.CreateFileViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.DeleteFilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.FilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.OptionsViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.SelectionViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.StorageInfoViewModel
import okio.Path
import com.flipperdevices.filemanager.listing.impl.R as FML

@Suppress("LongMethod")
@Composable
fun ComposableFileListScreen(
    path: Path,
    createFileViewModel: CreateFileViewModel,
    deleteFileViewModel: DeleteFilesViewModel,
    filesViewModel: FilesViewModel,
    optionsViewModel: OptionsViewModel,
    storageInfoViewModel: StorageInfoViewModel,
    selectionViewModel: SelectionViewModel,
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
    val selectionState by selectionViewModel.state.collectAsState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AnimatedContent(
                modifier = Modifier
                    .background(LocalPalletV2.current.surface.navBar.body.accentBrand),
                targetState = selectionState.isEnabled,
                contentKey = { it },
                transitionSpec = {
                    fadeIn()
                        .plus(slideInHorizontally())
                        .togetherWith(fadeOut().plus(slideOutHorizontally()))
                }
            ) { isSelectionEnabled ->
                if (isSelectionEnabled) {
                    CloseSelectionAppBar(
                        onClose = selectionViewModel::toggleMode,
                        onSelectAll = {
                            val paths = (filesListState as? FilesViewModel.State.Loaded)
                                ?.files
                                .orEmpty()
                                .map(ListingItem::fileName)
                                .map(path::resolve)
                            selectionViewModel.select(paths)
                        },
                        onDeselectAll = selectionViewModel::deselectAll
                    )
                } else {
                    OrangeAppBar(
                        title = stringResource(FML.string.fml_appbar_title),
                        endBlock = {
                            MoreIconComposable(
                                optionsState = optionsState,
                                onAction = optionsViewModel::onAction,
                                canCreateFiles = canCreateFiles,
                                onUploadClick = onUploadClick,
                                onSelectClick = selectionViewModel::toggleMode,
                                onCreateFolderClick = createFileViewModel::onCreateFolderClick,
                                onCreateFileClick = createFileViewModel::onCreateFileClick
                            )
                        },
                        onBack = onBack::invoke,
                    )
                }
            }
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
                        onCheckToggle = selectionViewModel::togglePath
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
                .padding(contentPadding),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                selectionState.isEnabled && filesListState is FilesViewModel.State.Loaded,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
            ) {
                BottomBarOptions(
                    canRename = true,
                    onMove = {},
                    onRename = {},
                    onDelete = {},
                    onExport = {},
                    onCopyTo = {}
                )
            }
        }
    }
}
