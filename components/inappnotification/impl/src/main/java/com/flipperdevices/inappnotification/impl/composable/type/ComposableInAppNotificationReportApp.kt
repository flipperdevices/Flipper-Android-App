package com.flipperdevices.inappnotification.impl.composable.type

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.inappnotification.impl.R

@Composable
fun ComposableInAppNotificationReportApp(modifier: Modifier = Modifier) {
    ComposableInAppNotificationBase(
        modifier = modifier,
        icon = {
            ComposableSaveIcon()
        },
        titleId = R.string.report_app_title,
        descId = R.string.report_app_desc,
        actionButton = null
    )
}
