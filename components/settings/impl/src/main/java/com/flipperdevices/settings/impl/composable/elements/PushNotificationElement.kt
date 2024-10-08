package com.flipperdevices.settings.impl.composable.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.notification.model.UpdateNotificationState
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.components.AnimatedSwitch
import com.flipperdevices.settings.impl.composable.components.SimpleElement

@Composable
fun PushNotificationElement(
    notificationState: UpdateNotificationState,
    modifier: Modifier = Modifier,
    onChangeNotificationState: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.clickableRipple {
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
            titleId = R.string.app_notification_title,
            descriptionId = R.string.app_notification_desc,
            titleTextStyle = LocalTypography.current.bodyR14
        )
        AnimatedSwitch(
            modifier = Modifier.padding(all = 12.dp),
            state = notificationState,
            onSwitch = onChangeNotificationState
        )
    }
}
