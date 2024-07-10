package com.flipperdevices.ifrmvp.core.ui.button.core

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit

@Composable
fun TextButton(
    text: String,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colors.primaryVariant,
    textColor: Color = MaterialTheme.colors.onPrimary,
    fontSize: TextUnit = MaterialTheme.typography.caption.fontSize,
    isEmulating: Boolean = false,
    onClick: (() -> Unit)?
) {
    SquareButton(
        onClick = onClick,
        background = background,
        modifier = modifier,
        isEmulating = isEmulating,
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.caption.copy(fontSize = fontSize),
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}
