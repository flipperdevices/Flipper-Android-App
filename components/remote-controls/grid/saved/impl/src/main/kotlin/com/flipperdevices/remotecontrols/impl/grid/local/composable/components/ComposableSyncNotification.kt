package com.flipperdevices.remotecontrols.impl.grid.local.composable.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.infrared.api.InfraredConnectionApi.InfraredEmulateState
import com.flipperdevices.remotecontrols.grid.saved.impl.R

@Composable
private fun notificationText(state: InfraredEmulateState): String {
    return when (state) {
        InfraredEmulateState.NOT_CONNECTED -> stringResource(R.string.sync_state_not_connected)
        InfraredEmulateState.CONNECTING,
        InfraredEmulateState.SYNCING -> stringResource(R.string.sync_state_syncing)

        InfraredEmulateState.UPDATE_FLIPPER,
        InfraredEmulateState.ALL_GOOD -> stringResource(R.string.sync_state_synced)
    }
}

@Composable
private fun animateNotificationColor(state: InfraredEmulateState): State<Color> {
    return animateColorAsState(
        targetValue = when (state) {
            InfraredEmulateState.UPDATE_FLIPPER,
            InfraredEmulateState.NOT_CONNECTED -> LocalPalletV2.current.action.danger.text.default

            InfraredEmulateState.CONNECTING,
            InfraredEmulateState.SYNCING -> LocalPalletV2.current.action.blue.text.default

            InfraredEmulateState.ALL_GOOD -> Color.Unspecified
        }
    )
}

@Composable
private fun NotificationIcon(state: InfraredEmulateState) {
    when (state) {
        InfraredEmulateState.NOT_CONNECTED -> {
            Icon(
                painter = painterResource(R.drawable.ic_not_connected),
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = LocalPalletV2.current.action.danger.text.default
            )
        }

        InfraredEmulateState.SYNCING,
        InfraredEmulateState.CONNECTING -> {
            val transition = rememberInfiniteTransition()
            val angle by transition.animateFloat(
                label = "Icon rotation",
                initialValue = 0F,
                targetValue = 360F,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 2000,
                        easing = LinearEasing
                    )
                ),
            )
            Icon(
                painter = painterResource(R.drawable.ic_syncing),
                contentDescription = null,
                modifier = Modifier
                    .size(12.dp)
                    .rotate(angle),
                tint = LocalPalletV2.current.text.link.default
            )
        }

        InfraredEmulateState.UPDATE_FLIPPER,
        InfraredEmulateState.ALL_GOOD -> {
            Icon(
                painter = painterResource(R.drawable.ic_synced),
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = LocalPalletV2.current.text.link.default
            )
        }
    }
}

@Composable
internal fun ComposableNotification(
    state: InfraredEmulateState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Crossfade(state) { state -> NotificationIcon(state) }
        Crossfade(state) { state ->
            val color by animateNotificationColor(state)
            Text(
                modifier = Modifier.weight(1f),
                text = notificationText(state),
                style = LocalTypography.current.subtitleM12,
                color = color
            )
        }
    }
}

@Preview
@Composable
private fun ComposableNotificationPreview() {
    FlipperThemeInternal {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ComposableNotification(InfraredEmulateState.CONNECTING)
            ComposableNotification(InfraredEmulateState.NOT_CONNECTED)
            ComposableNotification(InfraredEmulateState.ALL_GOOD)
        }
    }
}
