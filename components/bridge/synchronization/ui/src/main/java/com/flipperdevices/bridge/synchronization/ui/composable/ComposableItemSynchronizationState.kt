package com.flipperdevices.bridge.synchronization.ui.composable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import com.flipperdevices.bridge.synchronization.ui.R
import com.flipperdevices.bridge.synchronization.ui.model.ItemSynchronizationState
import com.flipperdevices.core.ui.ktx.elements.animatedDots
import com.flipperdevices.core.ui.ktx.image.painterResourceByKey
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.core.ui.res.R as DesignSystem

private const val ROTATE_DURATION_MS = 3000

@Composable
internal fun ComposableItemSynchronizationState(
    itemSynchronizationState: ItemSynchronizationState,
    withText: Boolean
) {
    val iconId = when (itemSynchronizationState) {
        ItemSynchronizationState.SYNCHRONIZED -> DesignSystem.drawable.ic_mini_synced
        ItemSynchronizationState.IN_PROGRESS -> DesignSystem.drawable.ic_mini_syncing
        ItemSynchronizationState.NOT_SYNCHRONIZED -> DesignSystem.drawable.ic_mini_sync_failed
    }

    val color = when (itemSynchronizationState) {
        ItemSynchronizationState.SYNCHRONIZED,
        ItemSynchronizationState.IN_PROGRESS -> LocalPallet.current.accentSecond
        ItemSynchronizationState.NOT_SYNCHRONIZED -> LocalPallet.current.text30
    }

    val descriptionId = when (itemSynchronizationState) {
        ItemSynchronizationState.SYNCHRONIZED -> R.string.synchronization_status_synced
        ItemSynchronizationState.IN_PROGRESS -> R.string.synchronization_status_syncing
        ItemSynchronizationState.NOT_SYNCHRONIZED -> R.string.synchronization_status_not_synced
    }
    var description = stringResource(descriptionId)
    var angel = 0f

    if (itemSynchronizationState == ItemSynchronizationState.IN_PROGRESS) {
        val infiniteTransition = rememberInfiniteTransition()
        val angelProgress by infiniteTransition.animateFloat(
            initialValue = 0F,
            targetValue = 360F,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = ROTATE_DURATION_MS, easing = LinearEasing)
            )
        )
        description += animatedDots()
        angel = angelProgress
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            modifier = Modifier.rotate(angel),
            painter = painterResourceByKey(iconId),
            contentDescription = stringResource(descriptionId),
            tint = color
        )

        if (withText) {
            Text(
                text = stringResource(descriptionId),
                style = LocalTypography.current.subtitleM10,
                color = color
            )
        }
    }
}
