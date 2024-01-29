package com.flipperdevices.updater.card.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.image.painterResourceByKey
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.model.UpdateErrorType
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ColumnScope.ComposableFirmwareUpdaterError(
    typeError: UpdateErrorType,
    modifier: Modifier = Modifier,
    retryUpdate: () -> Unit = {}
) {
    val title = stringResource(id = getTitleByUpdateError(typeError))
    val description = stringResource(id = getDescriptionByUpdateError(typeError))
    val image = painterResourceByKey(id = getImageByUpdateError(typeError))

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = image,
            contentDescription = title
        )
        Text(
            modifier = Modifier.padding(top = 4.dp, start = 12.dp, end = 12.dp),
            text = title,
            style = LocalTypography.current.bodyM14,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
            text = description,
            style = LocalTypography.current.bodyR14,
            color = LocalPallet.current.text30,
            textAlign = TextAlign.Center
        )
    }

    Text(
        modifier = Modifier
            .clickableRipple(onClick = retryUpdate)
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 8.dp),
        text = stringResource(R.string.updater_card_updater_error_retry),
        textAlign = TextAlign.Center,
        color = LocalPallet.current.accentSecond,
        style = LocalTypography.current.buttonM16
    )
}

@StringRes
fun getTitleByUpdateError(type: UpdateErrorType): Int {
    return when (type) {
        UpdateErrorType.NO_INTERNET -> R.string.update_card_error_no_internet_title
        UpdateErrorType.UNABLE_TO_SERVER -> R.string.update_card_error_server_request_title
        UpdateErrorType.NO_SD_CARD -> R.string.update_card_error_no_sd_title
    }
}

@StringRes
fun getDescriptionByUpdateError(type: UpdateErrorType): Int {
    return when (type) {
        UpdateErrorType.NO_INTERNET -> R.string.update_card_error_no_internet_desc
        UpdateErrorType.UNABLE_TO_SERVER -> R.string.update_card_error_server_request_desc
        UpdateErrorType.NO_SD_CARD -> R.string.update_card_error_no_sd_desc
    }
}

@Composable
fun getImageByUpdateError(type: UpdateErrorType): Int {
    return if (isSystemInDarkTheme()) {
        when (type) {
            UpdateErrorType.NO_INTERNET -> DesignSystem.drawable.ic_no_internet_dark
            UpdateErrorType.UNABLE_TO_SERVER -> DesignSystem.drawable.ic_server_error_dark
            UpdateErrorType.NO_SD_CARD -> DesignSystem.drawable.ic_no_sd_dark
        }
    } else {
        when (type) {
            UpdateErrorType.NO_INTERNET -> DesignSystem.drawable.ic_no_internet
            UpdateErrorType.UNABLE_TO_SERVER -> DesignSystem.drawable.ic_server_error
            UpdateErrorType.NO_SD_CARD -> DesignSystem.drawable.ic_no_sd
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableFirmwareUpdaterErrorPreview() {
    FlipperThemeInternal {
        Column(Modifier.background(LocalPallet.current.content)) {
            ComposableFirmwareUpdaterError(UpdateErrorType.NO_INTERNET)
            ComposableFirmwareUpdaterError(UpdateErrorType.UNABLE_TO_SERVER)
            ComposableFirmwareUpdaterError(UpdateErrorType.NO_SD_CARD)
        }
    }
}
