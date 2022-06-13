package com.flipperdevices.info.impl.compose.updater

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.compose.info.ComposableFirmwareVersionValue
import com.flipperdevices.info.shared.getColorByChannel
import com.flipperdevices.info.shared.getDescriptionByChannel
import com.flipperdevices.info.shared.getFullNameByChannel
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion

@Composable
fun ComposableUpdaterFirmwareVersionWithChoice(
    modifier: Modifier,
    updaterUIApi: UpdaterUIApi,
    version: FirmwareVersion
) {
    var showMenu by remember { mutableStateOf(false) }
    val updateCardApi = updaterUIApi.getUpdateCardApi()
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
            modifier = Modifier.clickable(
                indication = rememberRipple(),
                onClick = { showMenu = true },
                interactionSource = remember { MutableInteractionSource() }
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ComposableFirmwareVersionValue(version = version)
            Icon(
                modifier = Modifier
                    .padding(all = 4.dp),
                painter = painterResource(R.drawable.ic_more),
                contentDescription = stringResource(R.string.info_device_firmware_version_choice),
                tint = colorResource(DesignSystem.color.black_30)
            )

            ComposableDropMenuFirmwareBuild(
                showMenu = showMenu,
                coordinateMenuByY = positionYParentBox,
                onClickMenuItem = {
                    updateCardApi.onSelectChannel(it)
                    showMenu = false
                },
                onDismissMenu = {
                    showMenu = false
                }
            )
        }
    }
}

@Composable
fun ComposableDropMenuFirmwareBuild(
    showMenu: Boolean,
    coordinateMenuByY: Int,
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
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onDismissMenu.invoke()
                    }
            ) {
                Card(
                    modifier = Modifier
                        .width(wightPopupInDp)
                        .offset { IntOffset(coordinateMenuByX, coordinateMenuByY) }
                        .padding(end = 14.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    ComposableFirmwareColumn(onClickMenuItem)
                }
            }
        }
    }
}

@Composable
fun ComposableFirmwareColumn(
    onClickMenuItem: (FirmwareChannel) -> Unit = {}
) {
    Column {
        val channels = FirmwareChannel.values()
        channels.forEachIndexed { index, channel ->
            Column(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(),
                        onClick = { onClickMenuItem(channel) }
                    )
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = stringResource(getFullNameByChannel(channel)),
                    color = colorResource(getColorByChannel(channel)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500
                )
                Text(
                    text = stringResource(getDescriptionByChannel(channel)),
                    color = colorResource(DesignSystem.color.black_40),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400
                )
            }
            if (channels.lastIndex != index) {
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = colorResource(DesignSystem.color.black_12)
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
@Suppress("UnusedPrivateMember")
private fun ComposableFirmwareColumnPreview() {
    ComposableFirmwareColumn()
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
@Suppress("UnusedPrivateMember")
private fun ComposableDropMenuFirmwareBuildPreview() {
    ComposableDropMenuFirmwareBuild(showMenu = true, coordinateMenuByY = 0)
}
