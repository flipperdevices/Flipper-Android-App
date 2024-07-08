package com.flipperdevices.remotecontrols.impl.grid.composable

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.ifrmvp.core.ui.button.ButtonItemComposable
import com.flipperdevices.ifrmvp.core.ui.layout.core.GridItemComposable
import com.flipperdevices.ifrmvp.core.ui.layout.shared.ErrorComposable
import com.flipperdevices.ifrmvp.model.IfrButton
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.PageLayout
import com.flipperdevices.remotecontrols.grid.impl.R as GridR

@Composable
fun BoxWithConstraintsScope.ButtonsComposable(
    pageLayout: PageLayout?,
    onButtonClicked: (IfrButton, IfrKeyIdentifier) -> Unit,
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (pageLayout?.buttons.isNullOrEmpty()) {
        ErrorComposable(
            desc = stringResource(GridR.string.empty_page),
            onReload = onReload
        )
    }
    pageLayout?.buttons
        .orEmpty()
        .forEach { button ->
            GridItemComposable(
                modifier = modifier,
                position = button.position,
                content = {
                    ButtonItemComposable(
                        buttonData = button.data,
                        onKeyDataClicked = { keyIdentifier ->
                            onButtonClicked.invoke(button, keyIdentifier)
                        }
                    )
                }
            )
        }
}
