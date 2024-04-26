package com.flipperdevices.bridge.connection.screens.search

import com.flipperdevices.bridge.connection.config.api.model.FDeviceBaseModel
import no.nordicsemi.android.kotlin.ble.core.ServerDevice

data class ConnectionSearchItem(
    val device: ServerDevice,
    val savedDeviceModel: FDeviceBaseModel?
)
