package com.flipperdevices.filemanager.search.impl.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.core.ktx.jre.toFormattedSize
import com.flipperdevices.filemanager.search.impl.viewmodel.SearchViewModel
import com.flipperdevices.filemanager.ui.components.itemcard.FolderCardListComposable
import com.flipperdevices.filemanager.ui.components.itemcard.components.asPainter
import com.flipperdevices.filemanager.ui.components.itemcard.components.asTint
import com.flipperdevices.filemanager.ui.components.itemcard.model.ItemUiSelectionState
import okio.Path

@Suppress("FunctionNaming")
fun LazyListScope.FolderCardListLazyComposable(
    searchState: SearchViewModel.State.Loaded,
    onFolderSelect: (Path) -> Unit,
) {
    items(searchState.items, key = { it.fullPath.toString() }) { file ->
        FolderCardListComposable(
            modifier = Modifier
                .fillMaxWidth()
                .animateItem(),
            painter = file.instance.asPainter(),
            iconTint = file.instance.asTint(),
            title = file.instance.fileName,
            subtitle = file.fullPath.parent
                ?.toString()
                ?: file.instance.size.toFormattedSize(),
            isSubtitleLoading = false,
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
}
