package com.flipperdevices.bridge.api.utils

import java.util.UUID
import java.util.concurrent.TimeUnit

object Constants {
    const val DEVICENAME_PREFIX = "Flipper"
    val DEVICENAME_PREFIX_REGEXP = "Flipper .*".toPattern()

    object GenericService {
        val SERVICE_UUID: UUID = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb")
        val DEVICE_NAME: UUID = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb")
    }

    // BLE information service uuids: service uuid and characteristics uuids
    object BLEInformationService {
        val SERVICE_UUID: UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb")

        val MANUFACTURER: UUID = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb")

        // Example: f5, f6. Revision of hardware
        val HARDWARE_VERSION: UUID = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb")
        val SOFTWARE_VERSION: UUID = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb")
    }

    // BLE serial service uuids: service uuid and characteristics uuids
    object BLESerialService {
        val SERVICE_UUID: UUID = UUID.fromString("8fe5b3d5-2e7f-4a98-2a48-7acc60fe0000")

        val TX: UUID = UUID.fromString("19ed82ae-ed21-4c9d-4145-228e62fe0000")
        val RX: UUID = UUID.fromString("19ed82ae-ed21-4c9d-4145-228e61fe0000")
        val OVERFLOW: UUID = UUID.fromString("19ed82ae-ed21-4c9d-4145-228e63fe0000")
    }

    object BLE {
        private const val CONNECT_TIME_SEC = 30L
        val CONNECT_TIME_MS = TimeUnit.MILLISECONDS.convert(CONNECT_TIME_SEC, TimeUnit.SECONDS)
        const val RECONNECT_COUNT = 3
        const val RECONNECT_TIME_MS = 100L
        const val MAX_MTU = 512

        // How much time we next command
        // Small size increase count of ble packet
        // Large size increase waiting time for each command
        const val RPC_SEND_WAIT_TIMEOUT_MS = 100L

        const val DISCONNECT_TIMEOUT_MS = 10 * 1000L
    }
}
