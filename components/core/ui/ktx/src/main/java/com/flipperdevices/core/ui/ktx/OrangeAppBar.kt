package com.flipperdevices.core.ui.ktx

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun OrangeAppBar(
    @StringRes titleId: Int,
    onBack: (() -> Unit)? = null,
    endBlock: (@Composable () -> Unit)? = null
) {
    OrangeAppBar(
        title = stringResource(titleId),
        onBack = onBack,
        endBlock = endBlock
    )
}

@Composable
fun OrangeAppBar(
    title: String,
    onBack: (() -> Unit)? = null,
    endBlock: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalPallet.current.accent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            Image(
                modifier = Modifier
                    .padding(top = 11.dp, bottom = 11.dp, start = 16.dp, end = 2.dp)
                    .size(20.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(),
                        onClick = onBack
                    ),
                painter = painterResource(DesignSystem.drawable.ic_back),
                contentDescription = null
            )
        }
        Text(
            modifier = Modifier
                .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 11.dp)
                .weight(1f),
            text = title,
            style = LocalTypography.current.titleB20,
            color = LocalPallet.current.onAppBar
        )
        if (endBlock != null) {
            endBlock()
        }
    }
}


@Composable
fun OrangeAppBarWithIcon(
    @StringRes titleId: Int,
    onBack: (() -> Unit)? = null,
    @DrawableRes endIconId: Int,
    onEndClick: () -> Unit
) {
    OrangeAppBarWithIcon(
        title = stringResource(titleId),
        onBack = onBack,
        endIconId = endIconId,
        onEndClick = onEndClick
    )
}

@Composable
fun OrangeAppBarWithIcon(
    title: String,
    onBack: (() -> Unit)? = null,
    @DrawableRes endIconId: Int,
    onEndClick: () -> Unit
) {
    OrangeAppBar(
        title = title,
        onBack = onBack,
        endBlock = {
            Icon(
                modifier = Modifier
                    .padding(end = 14.dp)
                    .size(24.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(),
                        onClick = onEndClick
                    ),
                painter = painterResource(endIconId),
                contentDescription = null,
                tint = LocalPallet.current.onAppBar
            )
        }
    )
}
