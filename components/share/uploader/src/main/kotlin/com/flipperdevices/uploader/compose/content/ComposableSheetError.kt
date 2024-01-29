package com.flipperdevices.uploader.compose.content

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
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
import com.flipperdevices.share.uploader.R
import com.flipperdevices.uploader.models.ShareError
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
internal fun ComposableSheetError(
    typeError: ShareError,
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
                .clickableRipple(onClick = onRetry),
            text = stringResource(R.string.share_error_retry_btn),
            style = LocalTypography.current.buttonM16.copy(
                color = LocalPallet.current.accentSecond
            )
        )
    }
}

@StringRes
private fun getTitleByShareError(typeError: ShareError): Int {
    return when (typeError) {
        ShareError.NO_INTERNET_CONNECTION -> R.string.share_error_no_internet_title
        ShareError.CANT_CONNECT_TO_SERVER -> R.string.share_error_server_title
        ShareError.OTHER -> R.string.share_error_other_title
    }
}

@StringRes
private fun getDescriptionByShareError(typeError: ShareError): Int {
    return when (typeError) {
        ShareError.NO_INTERNET_CONNECTION -> R.string.share_error_no_internet_desc
        ShareError.CANT_CONNECT_TO_SERVER -> R.string.share_error_server_desc
        ShareError.OTHER -> R.string.share_error_other_desc
    }
}

@DrawableRes
@Composable
private fun getImageByShareError(typeError: ShareError): Int {
    return if (isSystemInDarkTheme()) {
        when (typeError) {
            ShareError.NO_INTERNET_CONNECTION -> DesignSystem.drawable.ic_no_internet_dark
            ShareError.CANT_CONNECT_TO_SERVER -> DesignSystem.drawable.ic_server_error_dark
            ShareError.OTHER -> DesignSystem.drawable.ic_warning_triangle
        }
    } else {
        when (typeError) {
            ShareError.NO_INTERNET_CONNECTION -> DesignSystem.drawable.ic_no_internet
            ShareError.CANT_CONNECT_TO_SERVER -> DesignSystem.drawable.ic_server_error
            ShareError.OTHER -> DesignSystem.drawable.ic_warning_triangle
        }
    }
}
