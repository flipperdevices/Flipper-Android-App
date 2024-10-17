package com.flipperdevices.ifrmvp.core.ui.button.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.flipperdevices.core.ui.ktx.onScrollHoldPress
import com.flipperdevices.ifrmvp.core.ui.layout.core.sf
import com.flipperdevices.ifrmvp.core.ui.util.GridConstants

// todo remove after design colors changed
val buttonBackgroundColor: Color
    @Composable
    get() = when (MaterialTheme.colors.isLight) {
        true -> Color(color = 0xFF616161)
        false -> Color(color = 0xFF303030)
    }
val buttonBackgroundVariantColor: Color
    @Composable
    get() = when (MaterialTheme.colors.isLight) {
        true -> Color(color = 0xFF919191)
        false -> Color(color = 0xFF616161)
    }

@Composable
fun SquareButton(
    onClick: ((ButtonClickEvent) -> Unit)?,
    background: Color,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .size(GridConstants.DEFAULT_BUTTON_SIZE.sf)
            .clip(RoundedCornerShape(8.sf))
            .background(background)
            .onScrollHoldPress { onClick?.invoke(it) },
        contentAlignment = Alignment.Center,
        content = {
            content.invoke(this)
        }
    )
}
