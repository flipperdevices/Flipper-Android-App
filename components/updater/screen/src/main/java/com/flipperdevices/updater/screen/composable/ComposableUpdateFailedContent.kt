package com.flipperdevices.updater.screen.composable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.markdown.ClickableUrlText
import com.flipperdevices.core.markdown.ComposableMarkdown
import com.flipperdevices.core.markdown.markdownColors
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.updater.screen.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
private fun ComposableUpdateFailedContent(
    @DrawableRes imageId: Int?,
    @StringRes titleId: Int?,
    @StringRes descriptionId: Int?,
    modifier: Modifier = Modifier,
    button: @Composable () -> Unit = {}
) {
    if (imageId != null) {
        Image(
            modifier = modifier.padding(start = 24.dp, end = 24.dp, bottom = 6.dp),
            painter = painterResource(imageId),
            contentDescription = titleId?.let { stringResource(it) }
        )
    }

    if (titleId != null) {
        Text(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 14.dp),
            text = stringResource(titleId),
            style = LocalTypography.current.bodyM14,
            textAlign = TextAlign.Center
        )
    }

    if (descriptionId != null) {
        ComposableMarkdown(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 14.dp),
            content = stringResource(descriptionId),
            colors = markdownColors(
                text = LocalPallet.current.text40
            )
        )
    }

    Box(Modifier.padding(start = 24.dp, end = 24.dp)) {
        button()
    }
}

@Composable
fun ComposableFailedUploadContent() {
    ComposableUpdateFailedContent(
        imageId = DesignSystem.drawable.ic_flipper_upload_failed,
        titleId = R.string.update_screen_failed_flipper_title,
        descriptionId = R.string.update_screen_failed_flipper_desc
    ) {
        ClickableUrlText(
            markdownResId = R.string.update_screen_failed_flipper_link
        )
    }
}

@Composable
fun ComposableFailedDownloadContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ComposableUpdateFailedContent(
        modifier = modifier,
        imageId = R.drawable.pic_server_error,
        titleId = R.string.update_screen_failed_network_title,
        descriptionId = R.string.update_screen_failed_network_desc
    ) {
        Text(
            modifier = Modifier.clickable(onClick = onRetry),
            text = stringResource(R.string.update_screen_failed_network_btn),
            style = LocalTypography.current.buttonM16,
            textAlign = TextAlign.Center,
            color = LocalPallet.current.accentSecond
        )
    }
}

@Composable
fun ComposableOutdatedApp() {
    ComposableUpdateFailedContent(
        imageId = if (MaterialTheme.colors.isLight) {
            DesignSystem.drawable.ic_firmware_application_deprecated
        } else {
            DesignSystem.drawable.ic_firmware_application_deprecated_dark
        },
        titleId = R.string.update_screen_failed_outdated_app_title,
        descriptionId = R.string.update_screen_failed_outdated_app_desc
    ) {
        ClickableUrlText(
            markdownResId = R.string.update_screen_failed_network_btn,
            style = LocalTypography.current.buttonM16.merge(
                TextStyle(
                    textAlign = TextAlign.Center,
                    color = LocalPallet.current.accentSecond
                )
            )
        )
    }
}

@Composable
fun ComposableInternalFlashFailed() {
    ComposableUpdateFailedContent(
        imageId = null,
        titleId = null,
        descriptionId = R.string.update_screen_failed_int_desc
    ) {
        ClickableUrlText(
            markdownResId = R.string.update_screen_failed_int_link
        )
    }
}

@Composable
fun ComposableInternalUpdateFailed() {
    ComposableUpdateFailedContent(
        imageId = null,
        titleId = R.string.update_screen_failed_int_update_dec,
        descriptionId = null
    )
}
