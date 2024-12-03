package com.flipperdevices.filemanager.listing.impl.composable.options

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

@Preview
@Composable
private fun BottomBarOptionsPreview() {
    FlipperThemeInternal {
        BottomBarOptions(
            onDelete = {},
            onExport = {},
            onRename = {},
            onCopyTo = {},
            onMove = {},
            canRename = true,
            canMove = true,
            canDelete = true,
            canExport = true
        )
    }
}
