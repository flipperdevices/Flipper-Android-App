package com.flipperdevices.info.impl.compose.elements

import androidx.annotation.ColorRes
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Suppress("LongParameterList")
fun ButtonElementCard(
    modifier: Modifier,
    iconAngel: Float = 0f,
    @StringRes titleId: Int,
    @DrawableRes iconId: Int,
    @ColorRes colorId: Int,
    onClick: (() -> Unit)?
) {
    var cardModifier = modifier
        .padding(horizontal = 14.dp)

    if (onClick != null) {
        cardModifier = cardModifier.clickable(
            indication = rememberRipple(),
            onClick = onClick,
            interactionSource = remember { MutableInteractionSource() }
        )
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(size = 10.dp)
    ) {
        val text = stringResource(titleId)
        val color = colorResource(colorId)
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = color
            )
        }
    }
}
