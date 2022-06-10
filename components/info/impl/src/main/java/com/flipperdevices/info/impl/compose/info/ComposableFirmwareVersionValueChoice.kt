package com.flipperdevices.info.impl.compose.info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.info.impl.R
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

    Box(
        modifier = modifier,
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

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                FirmwareChannel.values().forEach { channel ->
                    DropdownMenuItem(onClick = {
                        updateCardApi.onSelectChannel(channel)
                        showMenu = false
                    }) {
                        Text(text = stringResource(getFullNameByChannel(channel)))
                    }
                }
            }
        }
    }
}
