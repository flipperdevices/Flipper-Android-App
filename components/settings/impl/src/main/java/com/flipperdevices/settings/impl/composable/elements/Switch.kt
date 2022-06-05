package com.flipperdevices.settings.impl.composable.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.R

@Composable
fun Switch(
    state: Boolean,
    onSwitchState: (Boolean) -> Unit
) {
    androidx.compose.material.Switch(
        modifier = Modifier.padding(all = 12.dp),
        checked = state, onCheckedChange = onSwitchState,
        colors = SwitchDefaults.colors(
            checkedThumbColor = colorResource(R.color.accent),
            uncheckedThumbColor = colorResource(R.color.white_100),
            checkedTrackColor = colorResource(R.color.accent),
            uncheckedTrackColor = colorResource(R.color.black_40),
            uncheckedTrackAlpha = 0.5f
        )
    )
}
