package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableActionFlipperContent(
    @DrawableRes iconId: Int,
    @StringRes textId: Int
) {
    Icon(
        painter = painterResource(id = iconId),
        contentDescription = stringResource(id = textId),
        tint = LocalPallet.current.onFlipperButton
    )
    Spacer(Modifier.width(6.dp))
    Text(
        text = stringResource(id = textId),
        style = LocalTypography.current.flipperAction,
        color = LocalPallet.current.onFlipperButton
    )
}
