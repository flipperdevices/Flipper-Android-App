package com.flipperdevices.inappnotification.impl.composable.type

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.inappnotification.impl.R

@Composable
internal fun ComposableInAppNotificationError(
    error: InAppNotification.Error,
    onClickAction: () -> Unit
) {
    ComposableInAppNotificationBase(
        icon = {
            Image(
                modifier = Modifier.padding(12.dp).size(24.dp),
                painter = painterResource(id = R.drawable.pic_update_error),
                contentDescription = stringResource(error.titleId),
            )
        },
        titleId = error.titleId,
        descId = error.descId,
        actionButton = {
            val actionTextId = error.actionTextId
            val action = error.action
            if (actionTextId != null && action != null) {
                Text(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .clickable {
                            action()
                            onClickAction()
                        },
                    text = stringResource(actionTextId),
                    style = LocalTypography.current.subtitleM12,
                    color = LocalPallet.current.accentSecond
                )
            }
        }
    )
}

@Preview
@Composable
private fun ComposableInAppNotificationErrorPreview() {
    FlipperThemeInternal {
        ComposableInAppNotificationError(
            error = InAppNotification.Error(
                titleId = R.string.hide_app_title,
                descId = R.string.hide_app_desc,
                actionTextId = null,
                action = null
            ),
            onClickAction = {}
        )
    }
}
