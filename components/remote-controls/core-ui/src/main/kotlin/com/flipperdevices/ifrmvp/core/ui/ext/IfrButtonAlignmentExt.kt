package com.flipperdevices.ifrmvp.core.ui.ext

import androidx.compose.ui.Alignment
import com.flipperdevices.ifrmvp.model.IfrButton

fun IfrButton.Alignment.toComposeAlignment() = when (this) {
    IfrButton.Alignment.CENTER -> Alignment.Center
    IfrButton.Alignment.TOP_LEFT -> Alignment.TopStart
    IfrButton.Alignment.TOP_RIGHT -> Alignment.TopEnd
    IfrButton.Alignment.BOTTOM_LEFT -> Alignment.BottomStart
    IfrButton.Alignment.BOTTOM_RIGHT -> Alignment.BottomEnd
    IfrButton.Alignment.CENTER_LEFT -> Alignment.CenterStart
    IfrButton.Alignment.CENTER_RIGHT -> Alignment.CenterEnd
}
