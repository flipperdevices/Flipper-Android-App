package com.flipperdevices.filemanager.upload.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

@Preview
@Composable
private fun InProgressComposablePreview() {
    FlipperThemeInternal {
        InProgressComposable(
            fileName = "file_name.txt",
            uploadedFileSize = 1234L,
            uploadFileTotalSize = 1234567L,
            speed = 1222L
        )
    }
}
