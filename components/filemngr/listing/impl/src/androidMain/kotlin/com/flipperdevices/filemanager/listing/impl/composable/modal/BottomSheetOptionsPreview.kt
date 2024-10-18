package com.flipperdevices.filemanager.listing.impl.composable.modal

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import okio.Path.Companion.toPath

@Preview
@Composable
private fun BottomSheetOptionsPreview() {
    FlipperThemeInternal {
        BottomSheetOptionsContent(
            fileType = FileType.DIR,
            path = "some_file.ir".toPath(),
            onExport = {},
            onDelete = {},
            onCopyTo = {},
            onRename = {},
            onSelect = {},
            onMoveTo = {}
        )
    }
}
