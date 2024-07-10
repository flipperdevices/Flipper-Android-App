package com.flipperdevices.ifrmvp.core.ui.button.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.ifrmvp.core.ui.util.GridConstants

@Composable
fun SquareButton(
    onClick: (() -> Unit)?,
    background: Color,
    modifier: Modifier = Modifier,
    isEmulating: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .size(GridConstants.DEFAULT_BUTTON_SIZE)
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .then(
                if (onClick != null) {
                    Modifier.clickableRipple(
                        onClick = onClick,
                        enabled = !isEmulating
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center,
        content = content
    )
}
