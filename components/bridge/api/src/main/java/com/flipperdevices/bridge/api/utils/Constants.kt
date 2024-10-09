package com.flipperdevices.bridge.api.utils

import com.flipperdevices.core.data.SemVer
import java.util.UUID
import java.util.concurrent.TimeUnit

object Constants {
    const val DEVICENAME_PREFIX = "Flipper"
    const val MAC_PREFIX = "80:E1:26:"
    const val KEYS_DEFAULT_STORAGE = "/ext/"
    const val RPC_START_REQUEST_ARG = "RPC"
    val API_SUPPORTED_UNLOCK = SemVer(
        majorVersion = 0,
        minorVersion = 16
    )
    val API_SUPPORTED_FLIPPER_ERROR = SemVer(
        majorVersion = 0,
        minorVersion = 14
    )
    val API_SUPPORTED_REMOTE_EMULATE = SemVer(
        majorVersion = 0,
        minorVersion = 11
    )
    val API_SUPPORTED_LOAD_FAP = SemVer(
        majorVersion = 0,
        minorVersion = 18
    )
    val API_SUPPORTED_MD5_LISTING = SemVer(
        majorVersion = 0,
        minorVersion = 20
    )
    val API_SUPPORTED_INFRARED_EMULATE = SemVer(
        majorVersion = 0,
        minorVersion = 21
    )
    val API_SUPPORTED_GET_REQUEST = API_SUPPORTED_FLIPPER_ERROR
    const val LAGS_FLIPPER_DETECT_TIMEOUT_MS = 10 * 1000L // 10 seconds

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

        val API_VERSION: UUID = UUID.fromString("03f6666d-ae5e-47c8-8e1a-5d873eb5a933")
    }

    object BatteryService {
        val SERVICE_UUID: UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")

        val BATTERY_LEVEL: UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")
        val BATTERY_POWER_STATE: UUID = UUID.fromString("00002A1A-0000-1000-8000-00805f9b34fb")

        const val BATTERY_POWER_STATE_MASK: Byte = 0b0011_0000
    }

    // BLE serial service uuids: service uuid and characteristics uuids
    object BLESerialService {
        val SERVICE_UUID: UUID = UUID.fromString("8fe5b3d5-2e7f-4a98-2a48-7acc60fe0000")

        val TX: UUID = UUID.fromString("19ed82ae-ed21-4c9d-4145-228e62fe0000")
        val RX: UUID = UUID.fromString("19ed82ae-ed21-4c9d-4145-228e61fe0000")
        val OVERFLOW: UUID = UUID.fromString("19ed82ae-ed21-4c9d-4145-228e63fe0000")
        val RPC_STATE: UUID = UUID.fromString("19ed82ae-ed21-4c9d-4145-228e64fe0000")
    }

    object BLE {
        private const val CONNECT_TIME_SEC = 3L
        val CONNECT_TIME_MS = TimeUnit.MILLISECONDS.convert(CONNECT_TIME_SEC, TimeUnit.SECONDS)
        const val RECONNECT_COUNT = 1
        const val RECONNECT_TIME_MS = 100L
        const val MAX_MTU = 512

        // How much time we next command
        // Small size increase count of ble packet
        // Large size increase waiting time for each command
        const val RPC_SEND_WAIT_TIMEOUT_MS = 100L

        const val DISCONNECT_TIMEOUT_MS = 10 * 1000L
    }

    object PATH {
        const val MANIFEST_FILE = "/ext/Manifest"
        const val REGION_FILE = "/int/.region_data"
        const val APPS = "/ext/apps/"
    }
}
