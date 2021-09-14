package com.flipper.bridge.utils

import java.util.UUID
import java.util.concurrent.TimeUnit

object Constants {
    const val HEARTRATE_SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb"
    const val DEVICENAME_PREFIX = "Flipper"

    // BLE information service uuids: service uuid and characteristics uuids
    object BLEInformationService {
        val SERVICE_UUID: UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb")

        val MANUFACTURER: UUID = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb")
        val DEVICE_NAME: UUID = UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb")
        val HARDWARE_VERSION: UUID =
            UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb") // Example: f5, f6. Revision of hardware
        val SOFTWARE_VERSION: UUID = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb")
    }

    // BLE serial service uuids: service uuid and characteristics uuids
    object BLESerialService {
        val SERVICE_UUID: UUID = UUID.fromString("8fe5b3d5-2e7f-4a98-2a48-7acc60fe0000")

        val TX: UUID = UUID.fromString("19ed82ae-ed21-4c9d-4145-228e62fe0000")
        val RX: UUID = UUID.fromString("19ed82ae-ed21-4c9d-4145-228e61fe0000")
    }

    object BLE {
        val CONNECT_TIME_MS = TimeUnit.MILLISECONDS.convert(30L, TimeUnit.SECONDS)
        const val RECONNECT_COUNT = 3
        val RECONNECT_TIME_MS = TimeUnit.MILLISECONDS.toMillis(100L)
    }
}
