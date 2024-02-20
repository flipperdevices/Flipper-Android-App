package com.flipperdevices.settings.impl.composable.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.clickableRipple

@Composable
fun SwitchableElement(
    state: Boolean,
    modifier: Modifier = Modifier,
    @StringRes titleId: Int? = null,
    @StringRes descriptionId: Int? = null,
    onSwitchState: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.clickableRipple { onSwitchState(!state) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SimpleElement(
            Modifier.weight(weight = 1f),
            titleId,
            descriptionId
        )
        FlipperSwitch(state = state, onSwitchState = onSwitchState)
    }
}
