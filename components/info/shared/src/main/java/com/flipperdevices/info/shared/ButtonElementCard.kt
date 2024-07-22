package com.flipperdevices.info.shared

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ButtonElementRow(
    @StringRes titleId: Int,
    @DrawableRes iconId: Int,
    color: Color,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    iconAngel: Float = 0f,
    actionIconId: Int? = null
) {
    var rowModifier = modifier
        .fillMaxWidth()

    if (onClick != null) {
        rowModifier = rowModifier.clickableRipple(onClick = onClick)
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
            modifier = Modifier.weight(1f),
            text = text,
            style = LocalTypography.current.bodyM14,
            color = color
        )
        if (actionIconId != null) {
            Box(
                Modifier
                    .padding(horizontal = 8.dp, vertical = 13.dp)
                    .size(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(actionIconId),
                    contentDescription = text,
                    tint = LocalPallet.current.iconTint30
                )
            }
        }
    }
}
