package com.flipperdevices.updater.card.composable.dialogs

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.dialog.composable.FlipperDialog
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableSuccessfulUpdate(
    version: FirmwareVersion?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageId = if (MaterialTheme.colors.isLight) {
        DesignSystem.drawable.pic_update_successfull
    } else {
        DesignSystem.drawable.pic_update_successfull_dark
    }

    FlipperDialog(
        modifier = modifier,
        painter = painterResource(imageId),
        titleComposable = {
            Text(
                text = stringResource(R.string.update_card_dialog_successful_title),
                textAlign = TextAlign.Center,
                style = LocalTypography.current.bodyM14,
                color = LocalPallet.current.successfullyColor
            )
        },
        buttonText = stringResource(R.string.update_card_dialog_successful_btn),
        onClickButton = onDismiss,
        onDismissRequest = onDismiss,
        textComposable = {
            Text(
                text = buildAnnotatedString {
                    append(annotatedStringWithVersion(version, LocalPallet.current.text100))
                    append(' ')
                    append(stringResource(R.string.update_card_dialog_successful_desc))
                },
                textAlign = TextAlign.Center,
                style = LocalTypography.current.bodyR14
            )
        }
    )
}

@Composable
fun ComposableFailedUpdate(
    version: FirmwareVersion?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    FlipperDialog(
        modifier = modifier,
        painter = painterResource(
            id = if (MaterialTheme.colors.isLight) {
                R.drawable.pic_update_failed
            } else {
                R.drawable.pic_update_failed_dark
            }
        ),
        titleComposable = {
            Text(
                text = stringResource(R.string.update_card_dialog_failed_title),
                textAlign = TextAlign.Center,
                style = LocalTypography.current.bodyM14,
                color = LocalPallet.current.warningColor
            )
        },
        buttonText = stringResource(R.string.update_card_dialog_failed_btn),
        onClickButton = onDismiss,
        onDismissRequest = onDismiss,
        textComposable = {
            Text(
                text = buildAnnotatedString {
                    append(annotatedStringWithVersion(version, LocalPallet.current.text100))
                    append(' ')
                    append(stringResource(R.string.update_card_dialog_failed_desc))
                },
                textAlign = TextAlign.Center,
                style = LocalTypography.current.bodyR14
            )
        }
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableSuccessfulUpdateWithoutVersionPreview() {
    FlipperThemeInternal {
        ComposableSuccessfulUpdate(version = null, {})
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
        ComposableSuccessfulUpdate(version = version, {})
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableFailedUpdateWithoutVersionPreview() {
    FlipperThemeInternal {
        ComposableFailedUpdate(version = null, {})
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
        ComposableFailedUpdate(version = version, {})
    }
}
