package com.flipperdevices.uploader.compose.content

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.painterResourceByKey
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.share.api.ShareContentError
import com.flipperdevices.share.uploader.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
internal fun ComposableSheetError(
    typeError: ShareContentError,
    onRetry: () -> Unit
) {
    val title = stringResource(id = getTitleByShareError(typeError))
    val description = stringResource(id = getDescriptionByShareError(typeError))
    val image = painterResourceByKey(id = getImageByShareError(typeError))

    Column(
        modifier = Modifier.padding(top = 40.dp, bottom = 52.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(54.dp),
            painter = image,
            contentDescription = null
        )
        Text(
            text = title,
            style = LocalTypography.current.bodyM14
        )
        Text(
            text = description,
            style = LocalTypography.current.bodyR14.copy(
                color = LocalPallet.current.text30
            )
        )
        Text(
            modifier = Modifier
                .clickable(
                    indication = rememberRipple(),
                    onClick = onRetry,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            text = stringResource(R.string.share_error_retry_btn),
            style = LocalTypography.current.buttonM16.copy(
                color = LocalPallet.current.accentSecond
            )
        )
    }
}

@StringRes
private fun getTitleByShareError(typeError: ShareContentError): Int {
    return when (typeError) {
        ShareContentError.NO_INTERNET -> R.string.share_error_no_internet_title
        ShareContentError.SERVER_ERROR -> R.string.share_error_server_title
        ShareContentError.OTHER -> R.string.share_error_other_title
    }
}

@StringRes
private fun getDescriptionByShareError(typeError: ShareContentError): Int {
    return when (typeError) {
        ShareContentError.NO_INTERNET -> R.string.share_error_no_internet_desc
        ShareContentError.SERVER_ERROR -> R.string.share_error_server_desc
        ShareContentError.OTHER -> R.string.share_error_other_desc
    }
}

@DrawableRes
@Composable
private fun getImageByShareError(typeError: ShareContentError): Int {
    return if (isSystemInDarkTheme()) {
        when (typeError) {
            ShareContentError.NO_INTERNET -> DesignSystem.drawable.ic_no_internet_dark
            ShareContentError.SERVER_ERROR -> DesignSystem.drawable.ic_server_error_dark
            ShareContentError.OTHER -> DesignSystem.drawable.ic_warning_triangle
        }
    } else {
        when (typeError) {
            ShareContentError.NO_INTERNET -> DesignSystem.drawable.ic_no_internet
            ShareContentError.SERVER_ERROR -> DesignSystem.drawable.ic_server_error
            ShareContentError.OTHER -> DesignSystem.drawable.ic_warning_triangle
        }
    }
}
