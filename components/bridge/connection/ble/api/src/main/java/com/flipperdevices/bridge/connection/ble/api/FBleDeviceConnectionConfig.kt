package com.flipperdevices.bridge.connection.ble.api

import com.flipperdevices.bridge.connection.common.api.FDeviceConnectionConfig

data class FBleDeviceConnectionConfig(
    val macAddress: String
) : FDeviceConnectionConfig<FBleApi>()