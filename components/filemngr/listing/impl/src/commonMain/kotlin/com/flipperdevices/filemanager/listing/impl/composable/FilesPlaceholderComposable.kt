package com.flipperdevices.filemanager.listing.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.ui.Modifier
import com.flipperdevices.core.preference.pb.FileManagerOrientation
import com.flipperdevices.filemanager.ui.components.itemcard.FolderCardPlaceholderComposable

@Suppress("FunctionNaming")
fun LazyGridScope.FilesPlaceholderComposable(uiOrientation: FileManagerOrientation) {
    items(count = 6) {
        Box(modifier = Modifier.animateItem()) {
            FolderCardPlaceholderComposable(
                modifier = Modifier.fillMaxWidth(),
                orientation = uiOrientation,
            )
        }
    }
}
