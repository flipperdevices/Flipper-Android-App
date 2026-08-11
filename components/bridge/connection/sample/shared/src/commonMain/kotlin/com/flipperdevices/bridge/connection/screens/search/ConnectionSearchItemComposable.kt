package com.flipperdevices.bridge.connection.screens.search

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.connection.config.api.FDeviceType
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import flipperapp.components.bridge.connection.sample.shared.generated.resources.Res
import flipperapp.components.bridge.connection.sample.shared.generated.resources.material_ic_add_box
import flipperapp.components.bridge.connection.sample.shared.generated.resources.material_ic_bluetooth
import flipperapp.components.bridge.connection.sample.shared.generated.resources.material_ic_delete
import flipperapp.components.bridge.connection.sample.shared.generated.resources.material_ic_usb
import org.jetbrains.compose.resources.painterResource

@Composable
fun ConnectionSearchItemComposable(
    searchItem: ConnectionSearchItem,
    onDeviceClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Icon(
            modifier = Modifier
                .padding(16.dp)
                .size(24.dp),
            painter = painterResource(
                when (searchItem.deviceModel.type) {
                    FDeviceType.FLIPPER_ZERO_BLE -> Res.drawable.material_ic_bluetooth
                    FDeviceType.FLIPPER_ZERO_USB -> Res.drawable.material_ic_usb
                }
            ),
            contentDescription = null,
            tint = LocalPallet.current.text100
        )

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            text = searchItem.deviceModel.humanReadableName,
            color = LocalPallet.current.text100
        )

        Icon(
            modifier = Modifier
                .clickableRipple(onClick = onDeviceClick)
                .padding(16.dp)
                .size(24.dp),
            painter = painterResource(
                if (searchItem.isAdded) {
                    Res.drawable.material_ic_delete
                } else {
                    Res.drawable.material_ic_add_box
                }
            ),
            contentDescription = null,
            tint = LocalPallet.current.text100
        )
    }
}
