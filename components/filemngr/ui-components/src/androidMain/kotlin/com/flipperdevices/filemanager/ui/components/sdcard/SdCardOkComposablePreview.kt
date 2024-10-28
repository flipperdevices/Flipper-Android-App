package com.flipperdevices.filemanager.ui.components.sdcard

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

@Preview
@Composable
private fun SdCardMissingComposablePreview() {
    FlipperThemeInternal {
        SdCardMissingComposable()
    }
}

@Preview
@Composable
private fun SdCardOkComposablePreview() {
    FlipperThemeInternal {
        SdCardOkComposable(
            used = 1234562,
            total = 8929921
        )
    }
}

@Preview
@Composable
private fun SdCardLoadingComposablePreview() {
    FlipperThemeInternal {
        SdCardLoadingComposable()
    }
}
