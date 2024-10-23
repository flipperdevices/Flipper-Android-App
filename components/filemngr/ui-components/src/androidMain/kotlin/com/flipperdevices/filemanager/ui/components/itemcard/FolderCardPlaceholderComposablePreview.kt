package com.flipperdevices.filemanager.ui.components.itemcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.preference.pb.FileManagerOrientation
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

@Preview
@Composable
private fun FolderCardPlaceholderComposablePreview() {
    FlipperThemeInternal {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(
                FileManagerOrientation.LIST,
                FileManagerOrientation.GRID
            ).forEach { orientation ->
                FolderCardPlaceholderComposable(orientation = orientation)
            }
        }
    }
}
