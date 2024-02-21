package com.flipperdevices.inappnotification.impl.composable.type

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.inappnotification.impl.R
import com.flipperdevices.rootscreen.api.LocalDeeplinkHandler

@Composable
internal fun ComposableInAppNotificationReadyToUpdate(
    onAction: () -> Unit
) {
    val deeplinkHandler = LocalDeeplinkHandler.current
    ComposableInAppNotificationBase(
        icon = {
            Icon(
                modifier = Modifier.padding(12.dp).size(24.dp),
                painter = painterResource(id = R.drawable.ic_ready_to_update),
                contentDescription = stringResource(R.string.ready_to_update_title),
            )
        },
        titleId = R.string.ready_to_update_title,
        descId = R.string.ready_to_update_desc,
        actionButton = {
            ComposableInAppNotificationBaseActionText(
                titleId = R.string.ready_to_update_btn,
                onClick = {
                    deeplinkHandler.handleDeeplink(Deeplink.BottomBar.HubTab.FapHub.MainScreen.InstalledTab)
                    onAction()
                }
            )
        }
    )
}
