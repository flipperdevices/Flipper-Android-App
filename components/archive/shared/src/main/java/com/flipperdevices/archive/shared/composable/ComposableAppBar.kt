package com.flipperdevices.archive.shared.composable

import androidx.annotation.DrawableRes
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableAppBar(
    title: String,
    onBack: (() -> Unit)? = null
) {
    ComposableAppBar(title = title, onBack = onBack, endContent = null)
}

@Composable
fun ComposableAppBar(
    title: String,
    onBack: (() -> Unit)? = null,
    @DrawableRes iconId: Int,
    onIconClick: () -> Unit
) {
    ComposableAppBar(title, onBack) {
        Icon(
            modifier = it
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = onIconClick
                )
                .size(size = 24.dp),
            painter = painterResource(iconId),
            contentDescription = null,
            tint = LocalPallet.current.onAppBar
        )
    }
}

@Composable
fun ComposableAppBar(
    title: String,
    onBack: (() -> Unit)? = null,
    endContent: (@Composable (Modifier) -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalPallet.current.accent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            AppBarBackArrow(onBack)
        }
        Text(
            modifier = Modifier
                .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 11.dp)
                .weight(weight = 1f),
            text = title,
            style = LocalTypography.current.titleB20,
            color = LocalPallet.current.onAppBar
        )
        if (endContent != null) {
            endContent(
                Modifier.padding(end = 14.dp)
            )
        }
    }
}

@Composable
private fun AppBarBackArrow(onBack: () -> Unit) {
    Icon(
        modifier = Modifier
            .padding(start = 14.dp, top = 8.dp, bottom = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false),
                onClick = onBack
            )
            .size(size = 24.dp),
        painter = painterResource(DesignSystem.drawable.ic_back),
        contentDescription = null,
        tint = LocalPallet.current.onAppBar
    )
}
