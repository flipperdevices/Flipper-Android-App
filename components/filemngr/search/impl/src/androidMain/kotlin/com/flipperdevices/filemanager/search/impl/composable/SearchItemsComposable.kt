package com.flipperdevices.filemanager.search.impl.composable

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.core.ktx.jre.toFormattedSize
import com.flipperdevices.core.preference.pb.FileManagerOrientation
import com.flipperdevices.filemanager.search.impl.viewmodel.SearchViewModel
import com.flipperdevices.filemanager.ui.components.itemcard.FolderCardListComposable
import com.flipperdevices.filemanager.ui.components.itemcard.FolderCardPlaceholderComposable
import com.flipperdevices.filemanager.ui.components.itemcard.components.asPainter
import com.flipperdevices.filemanager.ui.components.itemcard.components.asTint
import com.flipperdevices.filemanager.ui.components.itemcard.model.ItemUiSelectionState
import okio.Path

@Composable
fun AnimatedContentScope.SearchItemsComposable(
    path: Path,
    currentDirState: SearchViewModel.State.Loaded,
    onFolderSelect: (Path) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(14.dp),
    ) {
        item { ListingTitleComposable(path) }
        items(currentDirState.items, key = { it.fullPath.toString() }) { file ->
            FolderCardListComposable(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
                painter = file.instance.asPainter(),
                iconTint = file.instance.asTint(),
                title = file.instance.fileName,
                subtitle = file.instance.size.toFormattedSize(),
                selectionState = ItemUiSelectionState.NONE,
                onClick = {
                    when (file.instance.fileType) {
                        FileType.DIR -> onFolderSelect.invoke(file.fullPath)

                        FileType.FILE ->
                            file.fullPath
                                .parent
                                ?.run(onFolderSelect::invoke)

                        else -> Unit
                    }
                },
                showEndBox = false,
                onCheckChange = {},
                onMoreClick = {},
            )
        }
        if (!currentDirState.isSearching && currentDirState.items.isEmpty()) {
            item {
                NoFilesComposable(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .animateItem()
                )
            }
        }
        if (currentDirState.isSearching) {
            items(count = 6) {
                FolderCardPlaceholderComposable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    orientation = FileManagerOrientation.LIST,
                )
            }
        }
    }
}
