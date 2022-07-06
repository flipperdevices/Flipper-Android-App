package com.flipperdevices.updater.screen.composable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.markdown.ClickableUrlText
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.updater.screen.R

@Composable
private fun ComposableUpdateFailedContent(
    @DrawableRes imageId: Int,
    @StringRes titleId: Int,
    @StringRes descriptionId: Int,
    button: @Composable () -> Unit
) {
    Image(
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 6.dp),
        painter = painterResource(imageId),
        contentDescription = stringResource(titleId)
    )

    Text(
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 14.dp),
        text = stringResource(titleId),
        fontWeight = FontWeight.W500,
        color = colorResource(DesignSystem.color.black_100),
        fontSize = 14.sp,
        textAlign = TextAlign.Center
    )

    Text(
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 14.dp),
        text = stringResource(descriptionId),
        fontWeight = FontWeight.W400,
        color = colorResource(DesignSystem.color.black_40),
        fontSize = 14.sp,
        textAlign = TextAlign.Center
    )

    Box(Modifier.padding(start = 24.dp, end = 24.dp)) {
        button()
    }
}

@Composable
fun ComposableFailedUploadContent() {
    ComposableUpdateFailedContent(
        imageId = R.drawable.ic_flipper_upload_failed,
        titleId = R.string.update_screen_failed_flipper_title,
        descriptionId = R.string.update_screen_failed_flipper_desc
    ) {
        ClickableUrlText(
            markdownResId = R.string.update_screen_failed_flipper_link
        )
    }
}

@Composable
fun ComposableFailedDownloadContent(onRetry: () -> Unit) {
    ComposableUpdateFailedContent(
        imageId = R.drawable.pic_server_error,
        titleId = R.string.update_screen_failed_network_title,
        descriptionId = R.string.update_screen_failed_network_desc
    ) {
        Text(
            modifier = Modifier.clickable(onClick = onRetry),
            text = stringResource(R.string.update_screen_failed_network_btn),
            fontWeight = FontWeight.W500,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = colorResource(DesignSystem.color.accent_secondary)
        )
    }
}
