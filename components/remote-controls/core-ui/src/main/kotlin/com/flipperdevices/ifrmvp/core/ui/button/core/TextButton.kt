package com.flipperdevices.ifrmvp.core.ui.button.core

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.flipperdevices.ifrmvp.core.ui.layout.core.sfp

@Composable
fun TextButton(
    text: String,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colors.primaryVariant,
    textColor: Color = MaterialTheme.colors.onPrimary,
    onClick: (() -> Unit)?
) {
    SquareButton(
        onClick = onClick,
        background = background,
        modifier = modifier,
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.caption,
                color = textColor,
                fontSize = 14.sfp,
                textAlign = TextAlign.Center,
                modifier = Modifier,
                lineHeight = 2.sfp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}
