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
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.share.api.ShareContentError
import com.flipperdevices.share.api.ShareContentError.CANT_CANNOT_TO_SERVER
import com.flipperdevices.share.api.ShareContentError.EXPIRED_LINK
import com.flipperdevices.share.api.ShareContentError.INVALID_FILE_FORMAT
import com.flipperdevices.share.api.ShareContentError.NO_INTERNET_CONNECTION
import com.flipperdevices.share.api.ShareContentError.OTHER
import com.flipperdevices.share.uploader.R

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
        NO_INTERNET_CONNECTION -> R.string.share_error_no_internet_title
        CANT_CANNOT_TO_SERVER -> R.string.share_error_server_title
        OTHER -> R.string.share_error_other_title
        INVALID_FILE_FORMAT, EXPIRED_LINK -> error("Invalid error type $typeError")
    }
}

@StringRes
private fun getDescriptionByShareError(typeError: ShareContentError): Int {
    return when (typeError) {
        NO_INTERNET_CONNECTION -> R.string.share_error_no_internet_desc
        CANT_CANNOT_TO_SERVER -> R.string.share_error_server_desc
        OTHER -> R.string.share_error_other_desc
        INVALID_FILE_FORMAT, EXPIRED_LINK -> error("Invalid error type $typeError")
    }
}

@DrawableRes
@Composable
private fun getImageByShareError(typeError: ShareContentError): Int {
    return if (isSystemInDarkTheme()) {
        when (typeError) {
            NO_INTERNET_CONNECTION -> DesignSystem.drawable.ic_no_internet_dark
            CANT_CANNOT_TO_SERVER -> DesignSystem.drawable.ic_server_error_dark
            OTHER -> DesignSystem.drawable.ic_warning_triangle
            INVALID_FILE_FORMAT, EXPIRED_LINK -> error("Invalid error type $typeError")
        }
    } else when (typeError) {
        NO_INTERNET_CONNECTION -> DesignSystem.drawable.ic_no_internet
        CANT_CANNOT_TO_SERVER -> DesignSystem.drawable.ic_server_error
        OTHER -> DesignSystem.drawable.ic_warning_triangle
        INVALID_FILE_FORMAT, EXPIRED_LINK -> error("Invalid error type $typeError")
    }
}
