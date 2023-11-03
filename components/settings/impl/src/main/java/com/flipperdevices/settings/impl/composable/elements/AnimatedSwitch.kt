package com.flipperdevices.settings.impl.composable.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.notification.model.UpdateNotificationState

@Composable
fun AnimatedSwitch(
    state: UpdateNotificationState,
    onSwitch: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) = Box(modifier) {
    AnimatedVisibility(
        state == UpdateNotificationState.ENABLED || state == UpdateNotificationState.DISABLED,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Switch(
            checked = state == UpdateNotificationState.ENABLED,
            onCheckedChange = onSwitch,
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = LocalPallet.current.disableSwitch,
                uncheckedTrackColor = LocalPallet.current.disableBackgroundSwitch,
                uncheckedTrackAlpha = 0.5f
            )
        )
    }
    AnimatedVisibility(
        state == UpdateNotificationState.IN_PROGRESS,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(12.dp)
                .size(24.dp),
            color = LocalPallet.current.accentSecond
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AnimatedSwitchPreview() {
    FlipperThemeInternal {
        var selectedState by remember {
            mutableStateOf(UpdateNotificationState.DISABLED)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                UpdateNotificationState.entries.forEach { state ->
                    Button(onClick = { selectedState = state }) {
                        Text(text = state.name)
                    }
                }
            }
            AnimatedSwitch(state = selectedState, onSwitch = {})
            UpdateNotificationState.entries.forEach { state ->
                AnimatedSwitch(state = state, onSwitch = {})
            }
        }
    }
}
