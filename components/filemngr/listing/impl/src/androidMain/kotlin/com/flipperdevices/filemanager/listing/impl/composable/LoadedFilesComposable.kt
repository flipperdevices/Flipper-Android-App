package com.flipperdevices.filemanager.listing.impl.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.core.ktx.jre.toFormattedSize
import com.flipperdevices.core.preference.pb.FileManagerOrientation
import com.flipperdevices.filemanager.listing.impl.model.BottomSheetFile
import com.flipperdevices.filemanager.listing.impl.viewmodel.DeleteFilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.FilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.SelectionViewModel
import com.flipperdevices.filemanager.ui.components.itemcard.FolderCardComposable
import com.flipperdevices.filemanager.ui.components.itemcard.FolderCardPlaceholderComposable
import com.flipperdevices.filemanager.ui.components.itemcard.components.asPainter
import com.flipperdevices.filemanager.ui.components.itemcard.components.asTint
import com.flipperdevices.filemanager.ui.components.itemcard.model.ItemUiSelectionState
import okio.Path

@Suppress("FunctionNaming", "LongParameterList")
fun LazyGridScope.LoadedFilesComposable(
    path: Path,
    deleteFileState: DeleteFilesViewModel.State,
    selectionState: SelectionViewModel.State,
    filesState: FilesViewModel.State.Loaded,
    orientation: FileManagerOrientation,
    canDeleteFiles: Boolean,
    onPathChanged: (Path) -> Unit,
    onCheckToggle: (Path) -> Unit,
    onDelete: (Path) -> Unit,
    onFileMoreClick: (BottomSheetFile) -> Unit
) {
    items(filesState.files) { file ->
        val isFileLoading = remember(deleteFileState.fileNamesOrNull) {
            deleteFileState.fileNamesOrNull
                .orEmpty()
                .contains(file.fileName)
        }
        Crossfade(isFileLoading) { animatedIsFileLoading ->
            if (animatedIsFileLoading) {
                FolderCardPlaceholderComposable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItemPlacement(),
                    orientation = orientation,
                )
            } else {
                val filePath = remember(path, file.fileName) {
                    path.resolve(file.fileName)
                }
                FolderCardComposable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItemPlacement(),
                    painter = file.asPainter(),
                    iconTint = file.asTint(),
                    title = file.fileName,
                    canDeleteFiles = canDeleteFiles,
                    subtitle = file.size.toFormattedSize(),
                    selectionState = when {
                        selectionState.selected.contains(filePath) -> ItemUiSelectionState.SELECTED
                        selectionState.isEnabled -> ItemUiSelectionState.UNSELECTED
                        else -> ItemUiSelectionState.NONE
                    },
                    onClick = {
                        if (file.fileType == FileType.DIR) {
                            onPathChanged.invoke(path / file.fileName)
                        }
                    },
                    onCheckChange = { onCheckToggle.invoke(filePath) },
                    onMoreClick = {
                        onFileMoreClick.invoke(
                            BottomSheetFile(
                                fileType = file.fileType ?: FileType.DIR,
                                path = filePath
                            )
                        )
                    },
                    onDelete = { onDelete.invoke(path.resolve(file.fileName)) },
                    orientation = orientation
                )
            }
        }
    }
}
