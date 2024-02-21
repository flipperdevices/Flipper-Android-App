package com.flipperdevices.inappnotification.impl.composable.type

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.inappnotification.impl.R

@Composable
internal fun ComposableInAppNotificationSavedKey(
    notification: InAppNotification.Successful
) {
    ComposableInAppNotificationBase(
        icon = {
            ComposableSaveIcon()
        },
        title = notification.title ?: notification.titleId?.let { stringResource(it) },
        desc = notification.desc ?: notification.descId?.let { stringResource(it) },
        actionButton = null
    )
}

private const val ICON_ANIMATION_DURATION_MS = 1000.0f

@Composable
internal fun ComposableSaveIcon() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.import_status_successful)
    )
    val localComposition = composition
    val speed = if (localComposition != null) {
        (localComposition.duration / ICON_ANIMATION_DURATION_MS)
    } else {
        1.0f
    }
    val progress by animateLottieCompositionAsState(
        speed = speed,
        composition = composition
    )

    LottieAnimation(
        modifier = Modifier
            .padding(all = 8.dp)
            .size(size = 32.dp),
        composition = composition,
        progress = { progress }
    )
}
