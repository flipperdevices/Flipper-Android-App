package com.flipperdevices.bridge.connection.transport.ble.api

import com.flipperdevices.bridge.connection.transport.common.api.FDeviceConnectionConfig
import java.util.UUID

data class FBleDeviceConnectionConfig(
    val macAddress: String,
    val serialConfig: FBleDeviceSerialConfig?
) : FDeviceConnectionConfig<FBleApi>()

data class FBleDeviceSerialConfig(
    val serialServiceUuid: UUID,
    val rxServiceCharUuid: UUID,
    val txServiceCharUuid: UUID,
    val overflowControl: OverflowControlConfig?,
    val resetCharUUID: UUID
)

data class OverflowControlConfig(
    val overflowServiceUuid: UUID
)
