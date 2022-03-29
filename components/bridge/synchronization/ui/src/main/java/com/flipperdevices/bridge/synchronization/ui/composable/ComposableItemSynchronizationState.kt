package com.flipperdevices.bridge.synchronization.ui.composable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.flipperdevices.bridge.synchronization.ui.R
import com.flipperdevices.bridge.synchronization.ui.model.ItemSynchronizationState
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.core.ui.composable.painterResourceByKey
import kotlin.math.roundToInt

private const val DOTS_COUNT = 3f
private const val DOTS_DURATION_MS = 3000

@Composable
internal fun ComposableItemSynchronizationState(
    itemSynchronizationState: ItemSynchronizationState,
    withText: Boolean
) {
    val iconId = when (itemSynchronizationState) {
        ItemSynchronizationState.SYNCHRONIZED -> R.drawable.ic_mini_synced
        ItemSynchronizationState.IN_PROGRESS -> R.drawable.ic_mini_syncing
        ItemSynchronizationState.NOT_SYNCHRONIZED -> R.drawable.ic_mini_sync_failed
    }

    val colorId = when (itemSynchronizationState) {
        ItemSynchronizationState.SYNCHRONIZED,
        ItemSynchronizationState.IN_PROGRESS -> DesignSystem.color.accent_secondary
        ItemSynchronizationState.NOT_SYNCHRONIZED -> DesignSystem.color.black_30
    }

    val descriptionId = when (itemSynchronizationState) {
        ItemSynchronizationState.SYNCHRONIZED -> R.string.synchronization_status_synced
        ItemSynchronizationState.IN_PROGRESS -> R.string.synchronization_status_syncing
        ItemSynchronizationState.NOT_SYNCHRONIZED -> R.string.synchronization_status_not_synced
    }
    var description = stringResource(descriptionId)
    if (itemSynchronizationState == ItemSynchronizationState.IN_PROGRESS) {
        val infiniteTransition = rememberInfiniteTransition()
        val dotsCount by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = DOTS_COUNT,
            animationSpec = infiniteRepeatable(
                animation = tween(DOTS_DURATION_MS, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        description += ".".repeat(dotsCount.roundToInt())
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResourceByKey(iconId),
            contentDescription = stringResource(descriptionId),
            tint = colorResource(colorId)
        )

        if (withText) {
            Text(
                text = stringResource(descriptionId),
                fontWeight = FontWeight.W500,
                fontSize = 10.sp,
                color = colorResource(colorId)
            )
        }
    }
}
