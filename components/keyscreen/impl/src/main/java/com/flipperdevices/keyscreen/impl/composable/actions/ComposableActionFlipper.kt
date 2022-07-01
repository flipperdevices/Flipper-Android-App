package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun ComposableActionFlipper(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: Int,
    @StringRes descriptionId: Int,
    descriptionColor: Color = LocalPallet.current.onButton100,
    tint: Color = LocalPallet.current.onButton100,
    onClick: () -> Unit
) {
    val descriptionText = stringResource(descriptionId)

    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(30.dp))
            .background(LocalPallet.current.accentSecond)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick
            )
            .then(modifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ComposableActionFlipperContent(iconId, descriptionText, descriptionColor, tint)
        }
    }
}

@Composable
fun ComposableActionFlipperHorizontal(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: Int,
    @StringRes descriptionId: Int,
    descriptionColor: Color = LocalPallet.current.onButton100,
    tint: Color = LocalPallet.current.onButton100,
    onClick: (() -> Unit)? = null
) {
    val descriptionText = stringResource(descriptionId)

    var boxModifier = modifier
        .clip(shape = RoundedCornerShape(30.dp))

    if (onClick != null) {
        boxModifier = boxModifier
            .background(LocalPallet.current.accentSecond)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick
            )
    } else {
        boxModifier = boxModifier.background(LocalPallet.current.text40)
    }

    Box(
        modifier = boxModifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ComposableActionFlipperContent(iconId, descriptionText, descriptionColor, tint)
        }
    }
}

@Composable
private fun ComposableActionFlipperContent(
    @DrawableRes iconId: Int,
    descriptionText: String,
    descriptionColor: Color,
    tint: Color
) {
    Icon(
        modifier = Modifier.size(size = 36.dp),
        painter = painterResource(iconId),
        contentDescription = descriptionText,
        tint = tint
    )
    Text(
        text = descriptionText,
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        color = descriptionColor
    )
}
