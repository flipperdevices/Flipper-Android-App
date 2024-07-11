package com.flipperdevices.ifrmvp.core.ui.layout.core

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.ifrmvp.core.ui.util.GridConstants

@Suppress("CompositionLocalAllowlist")
val LocalScaleFactor = compositionLocalOf { ScaleFactor(1f, 1f) }

val Number.sf: Dp
    @Composable
    get() = (LocalScaleFactor.current.value * this.toFloat()).dp

val Number.sfp: TextUnit
    @Composable
    get() = (this@sfp.toInt() * LocalScaleFactor.current.value).sp

class ScaleFactor(
    val widthScaleFactor: Float,
    val heightScaleFactor: Float,
) {
    val value = minOf(widthScaleFactor, heightScaleFactor)

    val width = GridConstants.SCALE_WIDTH * value
    val height = GridConstants.SCALE_HEIGHT * value
}

@Composable
fun BoxWithConstraintsScope.rememberScaleFactor(): ScaleFactor {
    return remember(maxWidth, maxHeight) {
        ScaleFactor(
            widthScaleFactor = maxWidth / GridConstants.SCALE_WIDTH.dp,
            heightScaleFactor = maxHeight / GridConstants.SCALE_HEIGHT.dp
        )
    }
}
