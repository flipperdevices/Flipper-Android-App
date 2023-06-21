package com.flipperdevices.inappnotification.impl.composable.type

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.ktx.elements.ComposableFlipperButton
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.inappnotification.impl.R

@Composable
internal fun ComposableInAppNotificationUpdateReady(
    notification: InAppNotification.UpdateReady,
    onClickAction: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(2f)
        ) {
            Text(
                text = stringResource(R.string.ready_update_title),
                style = LocalTypography.current.subtitleB12
            )
            Text(
                text = stringResource(R.string.ready_update_desc),
                style = LocalTypography.current.subtitleR12
            )
        }
        ComposableFlipperButton(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            textPadding = PaddingValues(vertical = 12.dp, horizontal = 4.dp),
            text = stringResource(R.string.ready_update_button),
            textStyle = TextStyle(
                fontSize = 12.sp
            ),
            onClick = {
                notification.action()
                onClickAction()
            }
        )
    }
}

@Preview(
    fontScale = 2.0f
)
@Composable
private fun ComposableInAppNotificationUpdateReadyPreviewX2Font() {
    FlipperThemeInternal {
        ComposableInAppNotificationUpdateReady(
            notification = InAppNotification.UpdateReady(
                durationMs = 0,
                action = { },
            ),
            {}
        )
    }
}

@Preview(
    fontScale = 1.5f
)
@Composable
private fun ComposableInAppNotificationUpdateReadyPreviewX15Font() {
    FlipperThemeInternal {
        ComposableInAppNotificationUpdateReady(
            notification = InAppNotification.UpdateReady(
                durationMs = 0,
                action = { },
            ),
            {}
        )
    }
}

@Preview
@Composable
private fun ComposableInAppNotificationUpdateReadyPreview() {
    FlipperThemeInternal {
        ComposableInAppNotificationUpdateReady(
            notification = InAppNotification.UpdateReady(
                durationMs = 0,
                action = { },
            ),
            {}
        )
    }
}
