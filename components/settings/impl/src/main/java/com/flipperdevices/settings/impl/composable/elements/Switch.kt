package com.flipperdevices.settings.impl.composable.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun Switch(
    state: Boolean,
    onSwitchState: (Boolean) -> Unit
) {
    androidx.compose.material.Switch(
        modifier = Modifier.padding(all = 12.dp),
        checked = state, onCheckedChange = onSwitchState,
        colors = SwitchDefaults.colors(
            checkedThumbColor = colorResource(DesignSystem.color.accent),
            uncheckedThumbColor = colorResource(DesignSystem.color.white_100),
            checkedTrackColor = colorResource(DesignSystem.color.accent),
            uncheckedTrackColor = colorResource(DesignSystem.color.black_40),
            uncheckedTrackAlpha = 0.5f
        )
    )
}
