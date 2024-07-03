package com.flipperdevices.ifrmvp.core.ui.button.core

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.flipperdevices.ifrmvp.core.ui.ext.asPainter
import com.flipperdevices.ifrmvp.core.ui.ext.tintFor
import com.flipperdevices.ifrmvp.model.buttondata.IconButtonData

@Composable
fun SquareIconButton(
    onClick: () -> Unit,
    painter: Painter,
    background: Color,
    iconTint: Color,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
) {
    SquareButton(
        modifier = modifier,
        onClick = onClick,
        background = background
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.fillMaxSize().padding(12.dp)
        )
    }
}

@Composable
fun SquareImageButton(
    onClick: () -> Unit,
    bitmap: ImageBitmap,
    background: Color,
    iconTint: Color,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
) {
    SquareButton(
        modifier = modifier,
        onClick = onClick,
        background = background
    ) {
        Icon(
            bitmap = bitmap,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.fillMaxSize().padding(12.dp)
        )
    }
}

@Composable
fun SquareIconButton(
    iconType: IconButtonData.IconType,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onClick: () -> Unit,
) {
    SquareButton(
        modifier = modifier,
        onClick = onClick,
        background = Color(0xFF303030)
    ) {
        Icon(
            painter = iconType.asPainter(),
            contentDescription = contentDescription,
            tint = iconType.tintFor(),
            modifier = Modifier.fillMaxSize().padding(12.dp)
        )
    }
}
