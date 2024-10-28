package com.flipperdevices.filemanager.search.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

@Preview
@Composable
private fun NoFilesComposablePreview() {
    FlipperThemeInternal {
        NoFilesComposable()
    }
}
