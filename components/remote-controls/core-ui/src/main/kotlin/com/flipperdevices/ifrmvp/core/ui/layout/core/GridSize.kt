package com.flipperdevices.ifrmvp.core.ui.layout.core

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp

data class GridSize(val width: Dp, val height: Dp)

@Composable
fun BoxWithConstraintsScope.rememberGridSize(
    maxRows: Int,
    maxColumns: Int,
): GridSize {
    val calculatedWidth = this.maxWidth
    val calculatedHeight = this.maxHeight
    return remember(calculatedWidth, calculatedHeight) {
        GridSize(
            width = calculatedWidth / maxColumns,
            height = calculatedHeight / maxRows
        )
    }
}
