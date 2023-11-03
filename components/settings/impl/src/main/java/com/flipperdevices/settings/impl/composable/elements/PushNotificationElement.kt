package com.flipperdevices.settings.impl.composable.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.notification.model.UpdateNotificationState
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.components.AnimatedSwitch
import com.flipperdevices.settings.impl.composable.components.SimpleElement

@Composable
fun PushNotificationElement(
    notificationState: UpdateNotificationState,
    onChangeNotificationState: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.clickableRipple {
            when (notificationState) {
                UpdateNotificationState.ENABLED -> onChangeNotificationState(false)
                UpdateNotificationState.DISABLED -> onChangeNotificationState(true)
                UpdateNotificationState.IN_PROGRESS -> {}
            }
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SimpleElement(
            modifier = Modifier.weight(weight = 1f),
            titleId = R.string.app_theme_options,
            descriptionId = R.string.app_theme_options_desc,
            titleTextStyle = LocalTypography.current.buttonB16
        )
        AnimatedSwitch(
            state = notificationState,
            onSwitch = onChangeNotificationState
        )
    }
}