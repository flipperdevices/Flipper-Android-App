package com.flipperdevices.filemanager.ui.components.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

@Preview
@Composable
@Suppress("MaximumLineLength", "MaxLineLength")
private fun ErrorContentComposablePreview() {
    FlipperThemeInternal {
        ErrorContentComposable(
            text = "Some unknown error happened :(",
            desc = "Sorry, but we don't know why that happened so it's impossible to resolve. Try to turn off and turn on the device! Good luck!",
            onRetry = {}
        )
    }
}
