package com.flipperdevices.keyscreen.shared.bar

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.keyscreen.shared.R

@Composable
fun ComposableBarBackIcon(modifier: Modifier, onClick: (() -> Unit)? = null) {
    var clickableModifier = modifier
    if (onClick != null) {
        clickableModifier = clickableModifier.clickableRipple(onClick)
    }

    Icon(
        modifier = clickableModifier
            .size(24.dp),
        painter = painterResource(DesignSystem.drawable.ic_back),
        contentDescription = stringResource(R.string.keyscreen_back_pic_desc)
    )
}
