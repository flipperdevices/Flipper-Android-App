package com.flipperdevices.filemanager.search.impl.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import com.flipperdevices.core.preference.pb.FileManagerOrientation
import com.flipperdevices.filemanager.ui.components.itemcard.FolderCardPlaceholderComposable

@Suppress("FunctionNaming")
fun LazyListScope.FolderCardPlaceholderLazyComposable() {
    items(count = 6) {
        FolderCardPlaceholderComposable(
            modifier = Modifier
                .fillMaxWidth()
                .animateItem(),
            orientation = FileManagerOrientation.LIST,
        )
    }
}
