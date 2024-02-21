package com.flipperdevices.inappnotification.impl.composable.type

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
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
