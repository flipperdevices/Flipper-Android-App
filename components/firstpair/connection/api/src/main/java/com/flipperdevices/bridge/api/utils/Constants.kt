package com.flipperdevices.bridge.api.utils

import java.util.UUID

object Constants {
    const val DEVICENAME_PREFIX = "Flipper"
    const val MAC_PREFIX = "80:E1:26:"

    // BLE information service uuids: service uuid and characteristics uuids
    object BLEInformationService {
        val SERVICE_UUID: UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb")

        // Example: f5, f6. Revision of hardware
        val SOFTWARE_VERSION: UUID = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb")
    }
}
