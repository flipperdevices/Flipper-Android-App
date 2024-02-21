package com.flipperdevices.inappnotification.impl.composable.type

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.inappnotification.impl.R

@Composable
internal fun ComposableInAppNotificationSelfUpdateStarted() {
    ComposableInAppNotificationBase(
        icon = {
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.pic_update_started),
                contentDescription = stringResource(R.string.started_update_title),
            )
        },
        titleId = R.string.started_update_title,
        descId = R.string.started_update_desc,
        actionButton = null
    )
}

@Preview(
    fontScale = 2.0f
)
@Composable
private fun ComposableInAppNotificationSelfUpdateStartedPreviewX2Font() {
    FlipperThemeInternal {
        ComposableInAppNotificationSelfUpdateStarted()
    }
}

@Preview(
    fontScale = 1.5f
)
@Composable
private fun ComposableInAppNotificationSelfUpdateStartedPreviewX15Font() {
    FlipperThemeInternal {
        ComposableInAppNotificationSelfUpdateStarted()
    }
}

@Preview
@Composable
private fun ComposableInAppNotificationSelfUpdateStartedPreview() {
    FlipperThemeInternal {
        ComposableInAppNotificationSelfUpdateStarted()
    }
}
