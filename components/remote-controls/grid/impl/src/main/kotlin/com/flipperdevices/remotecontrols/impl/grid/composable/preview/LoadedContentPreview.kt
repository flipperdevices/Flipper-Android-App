package com.flipperdevices.remotecontrols.impl.grid.composable.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.remotecontrols.impl.grid.composable.LoadedContent

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun LoadedContentPreview() {
    FlipperThemeInternal {
        LoadedContent(
            pagesLayout = KitchenLayoutFactory.create(),
            onButtonClick = { _, _ -> },
            onReload = {},
            emulatedKeyIdentifier = null
        )
    }
}
