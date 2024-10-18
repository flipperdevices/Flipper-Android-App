package com.flipperdevices.ifrmvp.core.ui.layout.shared

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.ifrmvp.core.ui.button.core.ButtonClickEvent
import com.flipperdevices.ifrmvp.model.IfrButton
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.PagesLayout

@Composable
fun GridPagesContent(
    pagesLayout: PagesLayout,
    onButtonClick: (IfrButton, ButtonClickEvent, IfrKeyIdentifier) -> Unit,
    emulatedKeyIdentifier: IfrKeyIdentifier?,
    isSyncing: Boolean,
    isConnected: Boolean,
    modifier: Modifier = Modifier,
    onReload: (() -> Unit)? = null,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart,
        content = {
            ButtonsComposable(
                pageLayout = pagesLayout.pages.firstOrNull(),
                emulatedKeyIdentifier = emulatedKeyIdentifier,
                onButtonClick = onButtonClick,
                onReload = onReload,
                isSyncing = isSyncing,
                isConnected = isConnected
            )
        }
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun LoadedContentEmptyPreview() {
    FlipperThemeInternal {
        GridPagesContent(
            pagesLayout = PagesLayout(emptyList()),
            onButtonClick = { _, _, _ -> },
            onReload = {},
            emulatedKeyIdentifier = null,
            isSyncing = false,
            isConnected = true
        )
    }
}
