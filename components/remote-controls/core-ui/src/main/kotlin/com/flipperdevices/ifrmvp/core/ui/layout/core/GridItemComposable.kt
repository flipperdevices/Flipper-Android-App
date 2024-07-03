package com.flipperdevices.ifrmvp.core.ui.layout.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import com.flipperdevices.ifrmvp.core.ui.ext.toComposeAlignment
import com.flipperdevices.ifrmvp.core.ui.util.GridConstants
import com.flipperdevices.ifrmvp.model.IfrButton

@Composable
fun BoxWithConstraintsScope.GridItemComposable(
    position: IfrButton.Position,
    maxRows: Int = GridConstants.MAX_ROWS,
    maxColumns: Int = GridConstants.MAX_COLUMNS,
    maxWidth: Dp = GridConstants.MAX_WIDTH,
    maxHeight: Dp = GridConstants.MAX_HEIGHT,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val offset = rememberDpOffset(
        position = position,
        maxRows = maxRows,
        maxColumns = maxColumns,
        maxWidth = maxWidth,
        maxHeight = maxHeight
    )
    val gridSize = rememberGridSize(
        maxRows = maxRows,
        maxColumns = maxColumns,
        maxWidth = maxWidth,
        maxHeight = maxHeight
    )
    Box(
        modifier = modifier
            .size(gridSize.width * position.containerWidth, gridSize.height * position.containerHeight)
            .offset(x = offset.x, y = offset.y)
            .background(Color(position.hashCode()).copy(alpha = .5f))
            .zIndex(position.zIndex),
        contentAlignment = position.alignment.toComposeAlignment(),
        content = content
    )
}
