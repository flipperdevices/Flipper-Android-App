package com.flipperdevices.bridge.connection.configbuilder.impl.builders

import com.flipperdevices.bridge.connection.transport.ble.api.FBleDeviceConnectionConfig
import com.flipperdevices.bridge.connection.transport.ble.api.FBleDeviceSerialConfig
import com.flipperdevices.bridge.connection.transport.ble.api.GATTCharacteristicAddress
import com.flipperdevices.bridge.connection.transport.ble.api.OverflowControlConfig
import com.flipperdevices.bridge.connection.transport.common.api.meta.TransportMetaInfoKey
import kotlinx.collections.immutable.persistentMapOf
import java.util.UUID
import javax.inject.Inject

private val INFORMATION_SERVICE_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb")
private val BATTERY_SERVICE_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")

class FlipperZeroBleBuilderConfig @Inject constructor() {
    fun build(
        address: String
    ) = FBleDeviceConnectionConfig(
        macAddress = address,
        serialConfig = FBleDeviceSerialConfig(
            serialServiceUuid = UUID.fromString("8fe5b3d5-2e7f-4a98-2a48-7acc60fe0000"),
            rxServiceCharUuid = UUID.fromString("19ed82ae-ed21-4c9d-4145-228e61fe0000"),
            txServiceCharUuid = UUID.fromString("19ed82ae-ed21-4c9d-4145-228e62fe0000"),
            resetCharUUID = UUID.fromString("19ed82ae-ed21-4c9d-4145-228e64fe0000"),
            overflowControl = OverflowControlConfig(
                overflowServiceUuid = UUID.fromString("19ed82ae-ed21-4c9d-4145-228e63fe0000")
            )
        ),
        metaInfoGattMap = persistentMapOf(
            TransportMetaInfoKey.DEVICE_NAME to GATTCharacteristicAddress(
                serviceAddress = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb"),
                characteristicAddress = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb")
            ),
            TransportMetaInfoKey.MANUFACTURER to GATTCharacteristicAddress(
                serviceAddress = INFORMATION_SERVICE_UUID,
                characteristicAddress = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb")
            ),
            TransportMetaInfoKey.HARDWARE_VERSION to GATTCharacteristicAddress(
                serviceAddress = INFORMATION_SERVICE_UUID,
                characteristicAddress = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb")
            ),
            TransportMetaInfoKey.SOFTWARE_VERSION to GATTCharacteristicAddress(
                serviceAddress = INFORMATION_SERVICE_UUID,
                characteristicAddress = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb")
            ),
            TransportMetaInfoKey.API_VERSION to GATTCharacteristicAddress(
                serviceAddress = INFORMATION_SERVICE_UUID,
                characteristicAddress = UUID.fromString("03f6666d-ae5e-47c8-8e1a-5d873eb5a933")
            ),
            TransportMetaInfoKey.BATTERY_LEVEL to GATTCharacteristicAddress(
                serviceAddress = BATTERY_SERVICE_UUID,
                characteristicAddress = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")
            ),
            TransportMetaInfoKey.BATTERY_POWER_STATE to GATTCharacteristicAddress(
                serviceAddress = BATTERY_SERVICE_UUID,
                characteristicAddress = UUID.fromString("00002A1A-0000-1000-8000-00805f9b34fb")
            )
        )
    )
}
