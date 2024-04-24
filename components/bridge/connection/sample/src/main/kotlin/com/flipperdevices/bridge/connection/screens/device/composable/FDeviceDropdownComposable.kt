package com.flipperdevices.bridge.connection.screens.device.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.connection.R
import com.flipperdevices.bridge.connection.config.api.model.FDeviceBaseModel
import com.flipperdevices.bridge.connection.screens.device.viewmodel.DevicesDropdownState
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.res.R as DrawableRes

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Suppress("LongMethod")
fun FDeviceDropdownComposable(
    devicesState: DevicesDropdownState,
    onDeviceSelect: (FDeviceBaseModel) -> Unit,
    onOpenSearch: () -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickableRipple { expanded = true }
            .border(1.dp, Color.Gray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            Text(
                text = devicesState.currentDevice?.humanReadableName
                    ?: stringResource(R.string.connection_dropdown_unknown)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                devicesState.devices.forEach { device ->
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onDeviceSelect(device)
                            expanded = false
                        }
                    ) {
                        Text(text = device.humanReadableName)
                    }
                }

                if (devicesState.currentDevice != null) {
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onDisconnect()
                            expanded = false
                        }
                    ) {
                        Text(text = "Disconnect current device")
                    }
                }

                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        expanded = false
                        onOpenSearch()
                    }
                ) {
                    Text(text = "Click to add device")
                }
            }
        }

        Icon(
            modifier = Modifier.padding(end = 16.dp),
            painter = painterResource(DrawableRes.drawable.ic_more),
            contentDescription = null
        )
    }
}
