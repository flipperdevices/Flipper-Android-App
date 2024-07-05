package com.flipperdevices.remotecontrols.impl.grid.composable

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.ifrmvp.core.ui.button.ButtonItemComposable
import com.flipperdevices.ifrmvp.core.ui.layout.core.GridItemComposable
import com.flipperdevices.ifrmvp.core.ui.layout.shared.ErrorComposable
import com.flipperdevices.ifrmvp.model.IfrButton
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.PageLayout

@Composable
fun BoxWithConstraintsScope.ButtonsComposable(
    pageLayout: PageLayout,
    onButtonClicked: (IfrButton, IfrKeyIdentifier) -> Unit,
) {
    if (pageLayout.buttons.isEmpty()) {
        ErrorComposable(desc = "Страница пустая")
    }
    pageLayout.buttons
        .forEach { button ->
            GridItemComposable(
                modifier = Modifier,
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
