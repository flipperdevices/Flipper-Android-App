package com.flipperdevices.filemanager.listing.impl.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.filemanager.listing.impl.viewmodel.StorageInfoViewModel
import com.flipperdevices.filemanager.ui.components.path.PathComposable
import okio.Path

@Suppress("FunctionNaming")
fun LazyGridScope.FilesSpanComposable(
    path: Path,
    storageInfoViewModel: StorageInfoViewModel,
    onPathChange: (Path) -> Unit
) {
    if (path.root != path) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            PathComposable(
                path = path,
                onRootPathClick = { path.root?.run(onPathChange) },
                onPathClick = onPathChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            )
        }
    } else {
        item(span = { GridItemSpan(maxLineSpan) }) {
            SdCardInfoComposable(storageInfoViewModel)
        }
    }
}
