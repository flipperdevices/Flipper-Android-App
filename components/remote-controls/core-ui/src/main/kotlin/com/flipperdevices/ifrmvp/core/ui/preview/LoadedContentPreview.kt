package com.flipperdevices.ifrmvp.core.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.ifrmvp.core.ui.layout.shared.GridPagesContent

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun LoadedContentPreview() {
    FlipperThemeInternal {
        GridPagesContent(
            pagesLayout = KitchenLayoutFactory.create(),
            onButtonClick = { _, _ -> },
            onReload = {},
            emulatedKeyIdentifier = null,
            isSyncing = false,
            isConnected = true
        )
    }
}
