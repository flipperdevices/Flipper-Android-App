package com.flipperdevices.share.receive.composable.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.image.painterResourceByKey
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.share.receive.R
import com.flipperdevices.share.receive.composable.components.ComposableKeySaveBar
import com.flipperdevices.share.receive.models.ReceiverError
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
internal fun ComposableKeyErrorScreen(
    typeError: ReceiverError,
    onCancel: () -> Unit,
    onRetry: () -> Unit
) {
    Column {
        ComposableKeySaveBar(onCancel)
        ComposableErrorContent(typeError, onRetry)
    }
}

@Composable
private fun ComposableErrorContent(typeError: ReceiverError, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(54.dp),
            painter = painterResourceByKey(id = getImageByShareError(typeError)),
            contentDescription = null
        )
        Text(
            text = stringResource(id = getTitleByShareError(typeError)),
            style = LocalTypography.current.bodyM14
        )
        Text(
            text = stringResource(id = getDescriptionByShareError(typeError)),
            style = LocalTypography.current.bodyR14.copy(
                color = LocalPallet.current.text30
            )
        )

        val isDisplayRetry = isCanUserRetryImportKey(typeError)
        if (isDisplayRetry) {
            Text(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clickableRipple(onClick = onRetry),
                text = stringResource(R.string.receive_retry_btn),
                style = LocalTypography.current.buttonM16.copy(
                    color = LocalPallet.current.accentSecond
                )
            )
        }
    }
}

@StringRes
private fun getTitleByShareError(typeError: ReceiverError): Int {
    return when (typeError) {
        ReceiverError.NO_INTERNET_CONNECTION -> R.string.receive_no_internet_title
        ReceiverError.CANT_CONNECT_TO_SERVER -> R.string.receive_error_server_title
        ReceiverError.INVALID_FILE_FORMAT -> R.string.receive_invalid_file_format
        ReceiverError.EXPIRED_LINK -> R.string.receive_invalid_expired_link
    }
}

@StringRes
private fun getDescriptionByShareError(typeError: ReceiverError): Int {
    return when (typeError) {
        ReceiverError.NO_INTERNET_CONNECTION -> R.string.receive_error_server_title
        ReceiverError.CANT_CONNECT_TO_SERVER -> R.string.receive_error_server_desc
        ReceiverError.INVALID_FILE_FORMAT -> R.string.receive_invalid_file_format_desc
        ReceiverError.EXPIRED_LINK -> R.string.receive_invalid_expired_link_desc
    }
}

@DrawableRes
@Composable
private fun getImageByShareError(typeError: ReceiverError): Int {
    return if (isSystemInDarkTheme()) {
        when (typeError) {
            ReceiverError.NO_INTERNET_CONNECTION -> DesignSystem.drawable.ic_no_internet_dark
            ReceiverError.CANT_CONNECT_TO_SERVER -> DesignSystem.drawable.ic_server_error_dark
            ReceiverError.INVALID_FILE_FORMAT -> R.drawable.ic_file_invalid_format
            ReceiverError.EXPIRED_LINK -> R.drawable.ic_expired_link
        }
    } else {
        when (typeError) {
            ReceiverError.NO_INTERNET_CONNECTION -> DesignSystem.drawable.ic_no_internet
            ReceiverError.CANT_CONNECT_TO_SERVER -> DesignSystem.drawable.ic_server_error
            ReceiverError.INVALID_FILE_FORMAT -> R.drawable.ic_file_invalid_format
            ReceiverError.EXPIRED_LINK -> R.drawable.ic_expired_link
        }
    }
}

private fun isCanUserRetryImportKey(typeError: ReceiverError): Boolean {
    return when (typeError) {
        ReceiverError.NO_INTERNET_CONNECTION, ReceiverError.CANT_CONNECT_TO_SERVER -> true
        ReceiverError.INVALID_FILE_FORMAT, ReceiverError.EXPIRED_LINK -> false
    }
}
