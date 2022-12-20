package com.flipperdevices.keyscreen.shared.bar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.keyscreen.shared.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableBarCancelIcon(
    modifier: Modifier,
    onClick: (() -> Unit)? = null
) {
    var clickableModifier = modifier
    if (onClick != null) {
        clickableModifier = clickableModifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = false),
            onClick = onClick
        )
    }

    Icon(
        modifier = clickableModifier
            .size(24.dp),
        painter = painterResource(DesignSystem.drawable.ic_close_icon),
        contentDescription = stringResource(R.string.keyscreen_cancel_pic_desc)
    )
}
