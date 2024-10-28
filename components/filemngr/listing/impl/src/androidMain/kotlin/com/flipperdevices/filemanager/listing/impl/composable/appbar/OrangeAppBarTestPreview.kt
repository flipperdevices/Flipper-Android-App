package com.flipperdevices.filemanager.listing.impl.composable.appbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

@Preview
@Composable
private fun OrangeAppBarTestPreview() {
    FlipperThemeInternal {
        CloseSelectionAppBar(
            onClose = {},
            onDeselectAll = {},
            onSelectAll = {}
        )
    }
}
