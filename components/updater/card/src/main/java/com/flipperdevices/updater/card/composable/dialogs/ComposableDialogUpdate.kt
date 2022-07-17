package com.flipperdevices.updater.card.composable.dialogs

import androidx.annotation.StringRes
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.dialog.composable.FlipperDialog
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.info.shared.getColorByChannel
import com.flipperdevices.info.shared.getTextByVersion
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion

@Composable
fun ComposableSuccessfulUpdate(
    version: FirmwareVersion?,
    onDismiss: () -> Unit
) {
    val imageId = if (MaterialTheme.colors.isLight) DesignSystem.drawable.pic_update_successfull
    else DesignSystem.drawable.pic_update_successfull_dark

    FlipperDialog(
        imageId = imageId,
        titleId = R.string.update_card_dialog_successful_title,
        buttonTextId = R.string.update_card_dialog_successful_btn,
        onClickButton = onDismiss,
        onDismissRequest = onDismiss,
        textComposable = {
            Text(
                text = buildAnnotatedStringWithColoredVersion(
                    version = version,
                    postfixId = R.string.update_card_dialog_successful_desc
                ),
                textAlign = TextAlign.Center,
                style = LocalTypography.current.bodyR14
            )
        }
    )
}

@Composable
fun ComposableFailedUpdate(
    version: FirmwareVersion?,
    onDismiss: () -> Unit
) {
    FlipperDialog(
        imageId = DesignSystem.drawable.pic_update_failed,
        titleId = R.string.update_card_dialog_failed_title,
        buttonTextId = R.string.update_card_dialog_failed_btn,
        onClickButton = onDismiss,
        onDismissRequest = onDismiss,
        textComposable = {
            Text(
                text = buildAnnotatedStringWithColoredVersion(
                    version = version,
                    postfixId = R.string.update_card_dialog_failed_desc
                ),
                textAlign = TextAlign.Center,
                style = LocalTypography.current.bodyR14
            )
        }
    )
}

@Composable
private fun buildAnnotatedStringWithColoredVersion(
    version: FirmwareVersion?,
    @StringRes postfixId: Int
): AnnotatedString {
    val channelColor = version?.let { getColorByChannel(it.channel) }
        ?: LocalPallet.current.channelFirmwareUnknown
    val versionText = version?.let { getTextByVersion(it) }
        ?: stringResource(R.string.update_card_dialog_unknown_version)
    val postfixText = stringResource(postfixId)
    val postfixColor = LocalPallet.current.text40
    return buildAnnotatedString {
        withStyle(style = SpanStyle(color = channelColor)) {
            append(versionText.capitalize(Locale.current))
        }
        append(' ')
        withStyle(style = SpanStyle(color = postfixColor)) {
            append(postfixText)
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableSuccessfulUpdateWithoutVersionPreview() {
    FlipperThemeInternal {
        ComposableSuccessfulUpdate(version = null) {}
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableSuccessfulUpdatePreview() {
    FlipperThemeInternal {
        val version = FirmwareVersion(
            channel = FirmwareChannel.DEV,
            version = "1.7.8"
        )
        ComposableSuccessfulUpdate(version = version) {}
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableFailedUpdateWithoutVersionPreview() {
    FlipperThemeInternal {
        ComposableFailedUpdate(version = null) {}
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableFailedUpdateUpdatePreview() {
    FlipperThemeInternal {
        val version = FirmwareVersion(
            channel = FirmwareChannel.DEV,
            version = "1.7.8"
        )
        ComposableFailedUpdate(version = version) {}
    }
}
