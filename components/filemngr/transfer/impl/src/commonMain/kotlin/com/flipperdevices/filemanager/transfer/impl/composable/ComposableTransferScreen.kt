package com.flipperdevices.filemanager.transfer.impl.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.preference.pb.FileManagerOrientation
import com.flipperdevices.filemanager.transfer.api.TransferDecomposeComponent.Param
import com.flipperdevices.filemanager.transfer.impl.viewmodel.FilesViewModel
import com.flipperdevices.filemanager.transfer.impl.viewmodel.OptionsViewModel
import com.flipperdevices.filemanager.ui.components.error.UnknownErrorComposable
import com.flipperdevices.filemanager.ui.components.error.UnsupportedErrorComposable
import com.flipperdevices.filemanager.ui.components.itemcard.FolderCardPlaceholderComposable
import okio.Path

@Suppress("LongMethod")
@Composable
fun ComposableTransferScreen(
    optionsState: OptionsViewModel.State,
    state: FilesViewModel.State,
    isMoving: Boolean,
    canMoveHere: Boolean,
    param: Param,
    onBack: () -> Unit,
    onOptionsAction: (OptionsViewModel.Action) -> Unit,
    onCreateFolder: () -> Unit,
    onPathChange: (Path) -> Unit,
    onMoveStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TransferAppBar(
                transferType = param.transferType,
                onBack = onBack,
                optionsState = optionsState,
                onOptionsAction = onOptionsAction,
                onCreateFolder = onCreateFolder
            )
        }
    ) { contentPaddings ->
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
            item(span = { GridItemSpan(maxLineSpan) }) {
                TransferPathComposable(
                    isMoving = isMoving,
                    path = param.path,
                    onPathChange = onPathChange
                )
            }

            when (val localState = state) {
                FilesViewModel.State.CouldNotListPath -> {
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                        content = { UnknownErrorComposable() }
                    )
                }

                is FilesViewModel.State.Loaded -> {
                    items(localState.files) { item ->
                        TransferFolderCardListComposable(
                            modifier = Modifier.animateItem(),
                            fullDirPath = param.path,
                            item = item,
                            onPathChange = onPathChange,
                            isMoving = isMoving
                        )
                    }
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                        content = { Box(Modifier.height(32.dp)) }
                    )
                }

                FilesViewModel.State.Loading -> {
                    items(count = 6) {
                        FolderCardPlaceholderComposable(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem(),
                            orientation = optionsState.orientation,
                        )
                    }
                }

                FilesViewModel.State.Unsupported -> {
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                        content = { UnsupportedErrorComposable() }
                    )
                }
            }
        }
        FullScreenMoveButtonComposable(
            isLoading = isMoving,
            isEnabled = !isMoving && canMoveHere,
            onClick = onMoveStart
        )
    }
}
