package com.flipperdevices.keyscreen.shared.bar

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R as DesignSystem

@Composable
fun ComposableBarSimpleText(
    modifier: Modifier,
    text: String,
    color: Color = colorResource(DesignSystem.color.black_40),
    onClick: (() -> Unit)? = null
) {
    var textModifier = modifier

    if (onClick != null) {
        textModifier = textModifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = false),
            onClick = onClick
        )
    }

    Text(
        modifier = textModifier,
        text = text,
        fontSize = 16.sp,
        color = color,
        fontWeight = FontWeight.W500
    )
}

@Composable
fun ComposableBarSimpleText(
    modifier: Modifier,
    @StringRes textId: Int,
    @ColorRes colorId: Int = DesignSystem.color.black_40,
    onClick: (() -> Unit)? = null
) {
    ComposableBarSimpleText(
        modifier, stringResource(textId), colorResource(colorId), onClick
    )
}
