package com.flipperdevices.remotecontrols.impl.grid.composable.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.ifrmvp.model.IfrButton
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.PagesLayout

@Composable
internal fun GridComposableLoadedContent(
    pagesLayout: PagesLayout,
    onButtonClick: (IfrButton, IfrKeyIdentifier) -> Unit,
    onReload: () -> Unit,
    emulatedKeyIdentifier: IfrKeyIdentifier?,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart,
        content = {
            ButtonsComposable(
                pageLayout = pagesLayout.pages.firstOrNull(),
                emulatedKeyIdentifier = emulatedKeyIdentifier,
                onButtonClick = onButtonClick,
                onReload = onReload
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
        GridComposableLoadedContent(
            pagesLayout = PagesLayout(emptyList()),
            onButtonClick = { _, _ -> },
            onReload = {},
            emulatedKeyIdentifier = null
        )
    }
}
