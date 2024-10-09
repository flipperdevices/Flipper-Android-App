package com.flipperdevices.inappnotification.impl.composable.type

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.inappnotification.impl.R

@Composable
internal fun ComposableInAppNotificationSelfUpdateError() {
    ComposableInAppNotificationBase(
        icon = {
            Image(
                modifier = Modifier.padding(12.dp).size(24.dp),
                painter = painterResource(id = R.drawable.pic_update_error),
                contentDescription = stringResource(R.string.error_update_title),
            )
        },
        titleId = R.string.error_update_title,
        descId = R.string.error_update_desc,
        actionButton = null
    )
}

@Preview(
    fontScale = 2.0f
)
@Composable
private fun PreviewComposableInAppNotificationSelfUpdateErrorPreviewX2Font() {
    FlipperThemeInternal {
        ComposableInAppNotificationSelfUpdateError()
    }
}

@Preview(
    fontScale = 1.5f
)
@Composable
private fun PreviewComposableInAppNotificationSelfUpdateErrorPreviewX15Font() {
    FlipperThemeInternal {
        ComposableInAppNotificationSelfUpdateError()
    }
}

@Preview
@Composable
private fun PreviewComposableInAppNotificationSelfUpdateErrorPreview() {
    FlipperThemeInternal {
        ComposableInAppNotificationSelfUpdateError()
    }
}
