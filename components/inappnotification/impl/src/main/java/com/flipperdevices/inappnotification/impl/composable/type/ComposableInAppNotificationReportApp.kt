package com.flipperdevices.inappnotification.impl.composable.type

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.inappnotification.impl.R

@Composable
fun ComposableInAppNotificationReportApp(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableSaveIcon()
        Column(modifier = Modifier.padding(top = 9.dp, bottom = 9.dp, end = 12.dp)) {
            Text(
                text = stringResource(R.string.report_app_title),
                style = LocalTypography.current.subtitleB12
            )
            Text(
                text = stringResource(R.string.report_app_desc),
                style = LocalTypography.current.subtitleR12
            )
        }
    }
}
