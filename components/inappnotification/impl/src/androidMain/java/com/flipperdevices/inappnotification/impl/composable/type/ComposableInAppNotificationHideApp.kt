package com.flipperdevices.inappnotification.impl.composable.type

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.inappnotification.impl.R

@Composable
fun ComposableInAppNotificationHideApp(
    notification: InAppNotification.HiddenApp,
    onClickAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    ComposableInAppNotificationBase(
        modifier = modifier,
        icon = {
            ComposableSaveIcon()
        },
        titleId = R.string.hide_app_title,
        descId = R.string.hide_app_desc,
        actionButton = {
            ComposableInAppNotificationBaseActionText(
                titleId = R.string.hide_app_btn,
                onClick = {
                    notification.action.invoke()
                    onClickAction()
                }
            )
        }
    )
}
