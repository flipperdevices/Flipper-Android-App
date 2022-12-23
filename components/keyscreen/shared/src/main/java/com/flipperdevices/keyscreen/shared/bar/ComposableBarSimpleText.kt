package com.flipperdevices.keyscreen.shared.bar

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableBarSimpleText(
    modifier: Modifier,
    text: String,
    color: Color = LocalPallet.current.text40,
    onClick: (() -> Unit)? = null
) {
    var textModifier = modifier

    if (onClick != null) {
        textModifier = textModifier.clickableRipple(onClick)
    }

    Text(
        modifier = textModifier,
        text = text,
        color = color,
        style = LocalTypography.current.buttonM16
    )
}
