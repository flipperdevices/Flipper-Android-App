package com.flipperdevices.remotecontrols.impl.grid.local.composable.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.remotecontrols.grid.saved.impl.R
import kotlinx.coroutines.delay

@Composable
internal fun ComposableNotification(
    icon: @Composable RowScope.() -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(LocalPalletV2.current.surface.popUp.body.default)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon.invoke(this)
        Text(
            modifier = Modifier
                .padding(top = 9.dp, bottom = 9.dp, end = 12.dp)
                .weight(1f),
            text = text,
            style = LocalTypography.current.subtitleB12
        )
    }
}

@Composable
fun ComposableSyncingNotification(modifier: Modifier = Modifier) {
    ComposableNotification(
        modifier = modifier,
        icon = {
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
                    .size(24.dp)
                    .rotate(angle),
                tint = LocalPalletV2.current.text.link.default
            )
        },
        text = stringResource(R.string.sync_state_syncing)
    )
}

@Composable
fun ComposableSyncedNotification(modifier: Modifier = Modifier) {
    ComposableNotification(
        modifier = modifier,
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_synced),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp),
                tint = LocalPalletV2.current.text.link.default
            )
        },
        text = stringResource(R.string.sync_state_synced)
    )
}

@Composable
fun ComposableNotConnectedNotification(modifier: Modifier = Modifier) {
    ComposableNotification(
        modifier = modifier,
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_not_connected),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp),
                tint = LocalPalletV2.current.illustration.blackAndWhite.black
            )
        },
        text = stringResource(R.string.sync_state_not_connected)
    )
}

private const val VISIBILITY_DURATION = 2000L

@Composable
fun ComposableSynchronizationNotification(state: SynchronizationState) {
    var isVisible by remember(Unit) { mutableStateOf(false) }
    LaunchedEffect(state, isVisible) {
        if (state is SynchronizationState.NotStarted) isVisible = false
        if (state is SynchronizationState.InProgress) isVisible = true
        if (state is SynchronizationState.Finished) {
            SnackbarDuration.Short
            delay(VISIBILITY_DURATION)
            isVisible = false
        }
    }
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        content = {
            when (state) {
                SynchronizationState.Finished -> ComposableSyncedNotification()
                is SynchronizationState.InProgress -> ComposableSyncingNotification()
                SynchronizationState.NotStarted -> Unit
            }
        }
    )
}

@Preview
@Composable
private fun ComposableNotificationPreview() {
    FlipperThemeInternal {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ComposableSyncingNotification()
            ComposableSyncedNotification()
            ComposableNotConnectedNotification()
        }
    }
}
