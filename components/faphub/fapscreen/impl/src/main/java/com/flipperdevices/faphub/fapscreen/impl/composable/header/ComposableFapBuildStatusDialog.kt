package com.flipperdevices.faphub.fapscreen.impl.composable.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.dialog.composable.FlipperDialog
import com.flipperdevices.core.ui.dialog.composable.FlipperDialogAndroid
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.placeholderByLocalProvider
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.appcard.composable.ComposableAppDialogBox
import com.flipperdevices.faphub.dao.api.model.FapBuildState
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.fapscreen.impl.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableFapBuildStatusDialog(
    fapItem: FapItem,
    onOpenDeviceTab: () -> Unit,
    onDismiss: () -> Unit
) {
    when (fapItem.upToDateVersion.buildState) {
        FapBuildState.READY -> LaunchedEffect(onDismiss) {
            onDismiss()
        }

        FapBuildState.READY_ON_RELEASE -> ComposableFapBuildStatusReadyToReleaseDialog(
            onDismiss = onDismiss,
            onOpenDeviceTab = onOpenDeviceTab
        )

        FapBuildState.BUILD_RUNNING -> ComposableFapBuildStatusBuildRunningDialog(
            fapItem,
            onDismiss
        )

        FapBuildState.UNSUPPORTED_APP -> ComposableFapBuildStatusUnsupportedAppDialog(
            fapItem,
            onDismiss
        )

        FapBuildState.FLIPPER_OUTDATED,
        FapBuildState.UNSUPPORTED_SDK -> ComposableFapBuildStatusFlipperOutdatedDialog(
            onDismiss = onDismiss,
            onOpenDeviceTab = onOpenDeviceTab
        )
    }
}

@Composable
private fun ComposableFapBuildStatusReadyToReleaseDialog(
    onDismiss: () -> Unit,
    onOpenDeviceTab: () -> Unit
) {
    FlipperDialog(
        title = stringResource(R.string.fapscreen_building_dialog_not_connected_title),
        textComposable = {
            val preText = stringResource(R.string.fapscreen_building_dialog_not_connected_desc_pre)
            val channelText = stringResource(
                R.string.fapscreen_building_dialog_not_connected_desc_channel
            )
            val postText = stringResource(
                R.string.fapscreen_building_dialog_not_connected_desc_post
            )
            val style = LocalTypography.current.bodyR14
            val releaseColor = LocalPallet.current.channelFirmwareRelease

            Text(
                text = remember(preText, channelText, postText, style, releaseColor) {
                    buildAnnotatedString {
                        append(preText)
                        append(' ')
                        withStyle(
                            style = style.toSpanStyle()
                                .copy(
                                    color = releaseColor
                                )
                        ) {
                            append(channelText)
                        }
                        append(' ')
                        append(postText)
                    }
                },
                color = LocalPallet.current.text40,
                style = LocalTypography.current.bodyR14,
                textAlign = TextAlign.Center
            )
        },
        buttonText = stringResource(R.string.fapscreen_building_dialog_not_connected_btn),
        onDismissRequest = onDismiss,
        onClickButton = {
            onDismiss()
            onOpenDeviceTab()
        },
        painter = painterResource(DesignSystem.drawable.ic_flipper_upload_failed)
    )
}

@Composable
private fun ComposableFapBuildStatusBuildRunningDialog(
    fapItem: FapItem,
    onDismiss: () -> Unit
) {
    FlipperDialog(
        title = stringResource(R.string.fapscreen_building_dialog_running_title),
        text = stringResource(R.string.fapscreen_building_dialog_running_desc),
        buttonText = stringResource(R.string.fapscreen_building_dialog_running_btn),
        onDismissRequest = onDismiss,
        onClickButton = onDismiss,
        imageComposable = {
            ComposableAppDialogBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 24.dp),
                fapItem = fapItem
            )
        },
    )
}

@Composable
private fun ComposableFapBuildStatusUnsupportedAppDialog(
    fapItem: FapItem,
    onDismiss: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    FlipperDialog(
        title = {
            Text(
                text = stringResource(R.string.fapscreen_building_dialog_outdated_app_title),
                style = LocalTypography.current.bodyM14,
                textAlign = TextAlign.Center,
                color = LocalPallet.current.text100
            )
        },
        text = {
            Text(
                text = stringResource(R.string.fapscreen_building_dialog_outdated_app_desc),
                color = LocalPallet.current.text40,
                style = LocalTypography.current.bodyR14,
                textAlign = TextAlign.Center
            )
        },
        onDismissRequest = onDismiss,
        buttons = {
            ComposableGitHubButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    uriHandler.openUri(fapItem.fapDeveloperInformation.githubRepositoryLink)
                }
            )
        },
        image = {
            ComposableAppDialogBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 24.dp),
                fapItem = fapItem
            )
        },
    )
}

@Composable
private fun ComposableGitHubButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(size = 30.dp))
            .placeholderByLocalProvider()
            .background(LocalPallet.current.accentSecond)
            .clickableRipple(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(R.drawable.ic_github),
            contentDescription = stringResource(R.string.fapscreen_building_dialog_outdated_app_btn),
            tint = LocalPallet.current.onFlipperButton
        )

        Text(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            text = stringResource(R.string.fapscreen_building_dialog_outdated_app_btn),
            color = LocalPallet.current.onFlipperButton,
            style = LocalTypography.current.buttonB16.copy(
                textDecoration = TextDecoration.Underline
            )
        )
    }
}

@Composable
private fun ComposableFapBuildStatusFlipperOutdatedDialog(
    onDismiss: () -> Unit,
    onOpenDeviceTab: () -> Unit
) {
    FlipperDialogAndroid(
        titleId = R.string.fapscreen_building_dialog_outdated_flipper_title,
        textId = R.string.fapscreen_building_dialog_outdated_flipper_desc,
        buttonTextId = R.string.fapscreen_building_dialog_outdated_flipper_btn,
        onDismissRequest = onDismiss,
        onClickButton = {
            onDismiss()
            onOpenDeviceTab()
        },
        imageId = DesignSystem.drawable.ic_firmware_flipper_deprecated
    )
}
