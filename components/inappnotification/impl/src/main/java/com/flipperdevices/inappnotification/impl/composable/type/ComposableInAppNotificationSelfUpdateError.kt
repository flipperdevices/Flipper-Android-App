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
internal fun ComposableInAppNotificationSelfUpdateError() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = R.drawable.pic_update_error),
            contentDescription = stringResource(R.string.error_update_title),
        )
        Column {
            Text(
                text = stringResource(R.string.error_update_title),
                style = LocalTypography.current.subtitleB12
            )
            Text(
                text = stringResource(R.string.error_update_desc),
                style = LocalTypography.current.subtitleR12
            )
        }
    }
}

@Preview(
    fontScale = 2.0f
)
@Composable
private fun ComposableInAppNotificationSelfUpdateErrorPreviewX2Font() {
    FlipperThemeInternal {
        ComposableInAppNotificationSelfUpdateError()
    }
}

@Preview(
    fontScale = 1.5f
)
@Composable
private fun ComposableInAppNotificationSelfUpdateErrorPreviewX15Font() {
    FlipperThemeInternal {
        ComposableInAppNotificationSelfUpdateError()
    }
}

@Preview
@Composable
private fun ComposableInAppNotificationSelfUpdateErrorPreview() {
    FlipperThemeInternal {
        ComposableInAppNotificationSelfUpdateError()
    }
}
