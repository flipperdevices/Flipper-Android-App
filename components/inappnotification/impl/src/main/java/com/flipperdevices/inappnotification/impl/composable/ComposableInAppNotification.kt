package com.flipperdevices.inappnotification.impl.composable

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.inappnotification.api.model.InAppNotification
import kotlin.math.max

const val VISIBLE_ANIMATION_MS = 1000

@Composable
fun ComposableInAppNotification(
    notification: InAppNotification,
    onNotificationHidden: () -> Unit
) {
    key(notification) {
        ComposableInAppNotificationCard(notification, onNotificationHidden)
    }
}

@Composable
private fun ComposableInAppNotificationCard(
    notification: InAppNotification,
    onNotificationHidden: () -> Unit
) {
    var visibleState by remember { mutableStateOf(false) }
    LaunchedEffect(notification) {
        visibleState = true
    }
    AnimatedVisibility(
        visible = visibleState,
        enter = fadeIn(animationSpec = tween(VISIBLE_ANIMATION_MS)),
        exit = fadeOut(animationSpec = tween(VISIBLE_ANIMATION_MS))
    ) {
        Card(
            modifier = Modifier
                .padding(all = 14.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(size = 8.dp),
            backgroundColor = colorResource(DesignSystem.color.black_4)
        ) {
            ComposableInAppNotificationCardContent(notification)
        }
    }
    DisposableEffect(notification) {
        val handler = Handler(Looper.getMainLooper())

        val fadeOutRunnable = {
            visibleState = false
        }
        val hiddenRunnable = {
            onNotificationHidden()
        }

        handler.postDelayed(
            fadeOutRunnable,
            max(0L, notification.durationMs - VISIBLE_ANIMATION_MS)
        )
        handler.postDelayed(
            hiddenRunnable,
            notification.durationMs
        )

        onDispose {
            handler.removeCallbacks(fadeOutRunnable)
            handler.removeCallbacks(hiddenRunnable)
        }
    }
}
