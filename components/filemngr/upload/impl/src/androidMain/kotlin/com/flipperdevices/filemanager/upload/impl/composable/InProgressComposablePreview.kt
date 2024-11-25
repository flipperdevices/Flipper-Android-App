package com.flipperdevices.filemanager.upload.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.filemanager.upload.api.UploaderDecomposeComponent

@Preview
@Composable
private fun InProgressComposablePreview() {
    FlipperThemeInternal {
        InProgressComposable(
            state = UploaderDecomposeComponent.State.Uploading(
                currentItemIndex = 0,
                totalItemsAmount = 3,
                uploadedSize = 123456,
                totalSize = 223456,
                currentItem = UploaderDecomposeComponent.UploadingItem(
                    fileName = "file_name.txt",
                    uploadedSize = 1234L,
                    totalSize = 1234567L
                )
            ),
            speed = 1222L
        )
    }
}
