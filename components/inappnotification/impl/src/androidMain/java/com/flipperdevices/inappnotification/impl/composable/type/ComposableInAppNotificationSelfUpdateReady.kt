package com.flipperdevices.inappnotification.impl.composable.type

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.ktx.elements.ComposableFlipperButton
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.inappnotification.impl.R

@Composable
internal fun ComposableInAppNotificationSelfUpdateReady(
    notification: InAppNotification.SelfUpdateReady,
    onClickAction: () -> Unit,
) {
    ComposableInAppNotificationBase(
        icon = null,
        titleId = R.string.ready_update_title,
        descId = R.string.ready_update_desc,
        actionButton = {
            ComposableFlipperButton(
                modifier = Modifier
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
    )
}

@Preview(
    fontScale = 2.0f
)
@Composable
private fun ComposableInAppNotificationUpdateReadyPreviewX2Font() {
    FlipperThemeInternal {
        ComposableInAppNotificationSelfUpdateReady(
            notification = InAppNotification.SelfUpdateReady(
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
        ComposableInAppNotificationSelfUpdateReady(
            notification = InAppNotification.SelfUpdateReady(
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
        ComposableInAppNotificationSelfUpdateReady(
            notification = InAppNotification.SelfUpdateReady(
                durationMs = 0,
                action = { },
            ),
            {}
        )
    }
}
