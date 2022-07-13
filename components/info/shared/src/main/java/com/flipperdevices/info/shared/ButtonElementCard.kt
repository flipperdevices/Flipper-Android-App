package com.flipperdevices.info.shared

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ButtonElementCard(
    modifier: Modifier,
    iconAngel: Float = 0f,
    @StringRes titleId: Int,
    @DrawableRes iconId: Int,
    color: Color,
    onClick: (() -> Unit)?
) {
    Card(
        modifier = modifier
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(size = 10.dp)
    ) {
        ButtonElementRow(modifier, iconAngel, titleId, iconId, color, onClick)
    }
}

@Composable
fun ButtonElementRow(
    modifier: Modifier = Modifier,
    iconAngel: Float = 0f,
    @StringRes titleId: Int,
    @DrawableRes iconId: Int,
    color: Color,
    onClick: (() -> Unit)?
) {
    var rowModifier = modifier
        .fillMaxWidth()

    if (onClick != null) {
        rowModifier = rowModifier.clickable(
            indication = rememberRipple(),
            onClick = onClick,
            interactionSource = remember { MutableInteractionSource() }
        )
    }

    val text = stringResource(titleId)
    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(
                    start = 12.dp,
                    top = 12.dp,
                    end = 10.dp,
                    bottom = 12.dp
                )
                .size(size = 24.dp)
                .rotate(iconAngel),
            painter = painterResource(iconId),
            contentDescription = text,
            tint = color
        )
        Text(
            text = text,
            style = LocalTypography.current.bodyR14,
            color = color
        )
    }
}
