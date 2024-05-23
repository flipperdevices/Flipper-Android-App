package com.flipperdevices.bridge.connection.configbuilder.impl.builders

import com.flipperdevices.bridge.connection.transport.ble.api.FBleDeviceConnectionConfig
import com.flipperdevices.bridge.connection.transport.ble.api.FBleDeviceSerialConfig
import com.flipperdevices.bridge.connection.transport.ble.api.OverflowControlConfig
import java.util.UUID
import javax.inject.Inject

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
        )
    )
}
