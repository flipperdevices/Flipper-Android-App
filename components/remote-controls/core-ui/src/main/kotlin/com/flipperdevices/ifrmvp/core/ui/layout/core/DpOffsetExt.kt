package com.flipperdevices.ifrmvp.core.ui.layout.core

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import com.flipperdevices.ifrmvp.model.IfrButton

@Composable
fun BoxWithConstraintsScope.rememberDpOffset(
    position: IfrButton.Position,
    maxRows: Int,
    maxColumns: Int,
    maxWidth: Dp,
    maxHeight: Dp
): DpOffset {
    check(position.y >= 0f && position.y < maxRows)
    check(position.x >= 0f && position.x < maxColumns)
    val calculatedWidth = this.maxWidth.coerceAtMost(maxWidth)
    val calculatedHeight = this.maxHeight.coerceAtMost(maxHeight)
    return remember(calculatedWidth, calculatedHeight, position) {
        DpOffset(
            calculatedWidth * (position.x.toFloat() / maxColumns),
            calculatedHeight * (position.y.toFloat() / maxRows)
        )
    }
}
