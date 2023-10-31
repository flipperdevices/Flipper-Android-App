package com.flipperdevices.settings.impl.composable.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun Switch(
    state: Boolean,
    modifier: Modifier = Modifier,
    onSwitchState: (Boolean) -> Unit
) {
    androidx.compose.material.Switch(
        modifier = modifier.padding(horizontal = 12.dp),
        checked = state,
        onCheckedChange = onSwitchState,
        colors = SwitchDefaults.colors(
            uncheckedThumbColor = LocalPallet.current.disableSwitch,
            uncheckedTrackColor = LocalPallet.current.disableBackgroundSwitch,
            uncheckedTrackAlpha = 0.5f
        )
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun SwitchPreview() {
    FlipperThemeInternal {
        Column {
            Switch(
                state = true,
                onSwitchState = {}
            )

            Switch(
                state = false,
                onSwitchState = {}
            )
        }
    }
}
