package com.flipper.bridge.utils

import java.util.UUID

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

    object BLE {
        const val RECONNECT_COUNT = 3
        const val RECONNECT_TIME = 100
    }
}
