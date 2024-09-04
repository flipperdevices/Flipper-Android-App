package com.flipperdevices.bridge.connection.transport.ble.api

import com.flipperdevices.bridge.connection.transport.common.api.FDeviceConnectionConfig
import com.flipperdevices.bridge.connection.transport.common.api.meta.TransportMetaInfoKey
import kotlinx.collections.immutable.ImmutableMap
import java.util.UUID

data class FBleDeviceConnectionConfig(
    val macAddress: String,
    val serialConfig: FBleDeviceSerialConfig?,
    val metaInfoGattMap: ImmutableMap<TransportMetaInfoKey, GATTCharacteristicAddress>
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
