package com.flipperdevices.inappnotification.impl.composable.type

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
internal fun ComposableInAppNotificationBase(
    icon: (@Composable () -> Unit)?,
    @StringRes titleId: Int?,
    @StringRes descId: Int?,
    actionButton: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier
) {
    ComposableInAppNotificationBase(
        icon = icon,
        title = titleId?.let { stringResource(it) },
        desc = descId?.let { stringResource(it) },
        actionButton = actionButton,
        modifier = modifier
    )
}

@Composable
internal fun ComposableInAppNotificationBase(
    icon: (@Composable () -> Unit)?,
    title: String?,
    desc: String?,
    actionButton: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.invoke()
        Column(
            Modifier
                .padding(top = 9.dp, bottom = 9.dp, end = 12.dp)
                .weight(1f)
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = LocalTypography.current.subtitleB12
                )
            }
            if (desc != null) {
                Text(
                    text = desc,
                    style = LocalTypography.current.subtitleR12
                )
            }
        }
        actionButton?.invoke()
    }
}

@Composable
internal fun ComposableInAppNotificationBaseActionText(
    @StringRes titleId: Int,
    onClick: () -> Unit
) {
    Text(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(end = 12.dp),
        text = stringResource(titleId),
        style = LocalTypography.current.subtitleB12,
        color = LocalPallet.current.actionOnFlipperEnable
    )
}
