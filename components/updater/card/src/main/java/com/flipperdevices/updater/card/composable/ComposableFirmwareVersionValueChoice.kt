package com.flipperdevices.updater.card.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.info.shared.ComposableDeviceInfoRowText
import com.flipperdevices.info.shared.getColorByChannel
import com.flipperdevices.info.shared.getDescriptionByChannel
import com.flipperdevices.info.shared.getFullNameByChannel
import com.flipperdevices.info.shared.getTextByVersion
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableUpdaterFirmwareVersionWithChoice(
    version: FirmwareVersion?,
    modifier: Modifier = Modifier,
    onSelectFirmwareChannel: (FirmwareChannel) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    var positionYParentBox by remember { mutableStateOf(0) }

    Box(
        modifier = modifier.onGloballyPositioned {
            val size = it.size
            val coordinateByY = it.positionInRoot().y + size.height
            positionYParentBox = coordinateByY.toInt()
        },
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier.clickableRipple { showMenu = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (version == null) {
                ComposablePlaceholderFirmwareBuild()
            } else {
                ComposableDeviceInfoRowText(
                    text = getTextByVersion(version),
                    color = getColorByChannel(version.channel)
                )
                Icon(
                    modifier = Modifier
                        .padding(all = 4.dp),
                    painter = painterResource(DesignSystem.drawable.ic_more),
                    contentDescription = stringResource(
                        id = R.string.updater_card_firmware_version_choice
                    ),
                    tint = LocalPallet.current.iconTint30
                )

                ComposableDropMenuFirmwareBuild(
                    showMenu = showMenu,
                    coordinateMenuByY = positionYParentBox,
                    onClickMenuItem = {
                        onSelectFirmwareChannel(it)
                        showMenu = false
                    },
                    onDismissMenu = {
                        showMenu = false
                    }
                )
            }
        }
    }
}

@Composable
fun ComposablePlaceholderFirmwareBuild(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(16.dp)
            .width(80.dp)
            .placeholderConnecting()
    )
}

@Composable
fun ComposableDropMenuFirmwareBuild(
    showMenu: Boolean,
    coordinateMenuByY: Int,
    modifier: Modifier = Modifier,
    onClickMenuItem: (FirmwareChannel) -> Unit = {},
    onDismissMenu: () -> Unit = {}
) {
    val wightDeviceInDp = LocalConfiguration.current.screenWidthDp.dp
    val wightPopupInDp = 240.dp

    val coordinateMenuByX =
        LocalDensity.current.run { (wightDeviceInDp - wightPopupInDp).toPx() }.toInt()

    if (showMenu) {
        Dialog(
            onDismissRequest = onDismissMenu,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .clickable(onClick = onDismissMenu)
            ) {
                Card(
                    modifier = Modifier
                        .width(wightPopupInDp)
                        .offset { IntOffset(coordinateMenuByX, coordinateMenuByY) }
                        .padding(end = 14.dp)
                ) {
                    ComposableFirmwareColumn(onClickMenuItem = onClickMenuItem)
                }
            }
        }
    }
}

@Composable
fun ComposableFirmwareColumn(
    modifier: Modifier = Modifier,
    onClickMenuItem: (FirmwareChannel) -> Unit
) {
    Column(modifier = modifier) {
        val channels = FirmwareChannel.values().filter { it != FirmwareChannel.UNKNOWN }
        channels.forEachIndexed { index, channel ->
            Column(
                modifier = Modifier
                    .clickableRipple { onClickMenuItem(channel) }
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = stringResource(getFullNameByChannel(channel)),
                    color = getColorByChannel(channel),
                    style = LocalTypography.current.bodyM14
                )
                Text(
                    text = stringResource(getDescriptionByChannel(channel)),
                    color = LocalPallet.current.text40,
                    style = LocalTypography.current.subtitleR12
                )
            }
            if (channels.lastIndex != index) {
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = LocalPallet.current.divider12
                )
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableUpdaterFirmwareVersionWithChoicePreview() {
    FlipperThemeInternal {
        val firmwareVersion = FirmwareVersion(
            channel = FirmwareChannel.RELEASE,
            version = "1.1.1"
        )
        Column {
            ComposableUpdaterFirmwareVersionWithChoice(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .background(LocalPallet.current.background),
                version = firmwareVersion
            )
            ComposableUpdaterFirmwareVersionWithChoice(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .background(LocalPallet.current.background),
                version = null
            )
        }
    }
}
