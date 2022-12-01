package com.flipperdevices.bridge.api.model

import com.flipperdevices.core.data.SemVer

data class FlipperRpcInformation(
    val internalStorageStats: StorageStats? = null,
    val externalStorageStats: StorageStats? = null,
    val flipperDeviceInfo: FlipperDeviceInfo = FlipperDeviceInfo(),
    val firmware: FirmwareInfo = FirmwareInfo(),
    val radioStack: RadioStackInfo = RadioStackInfo(),
    val otherFields: Map<String, String> = emptyMap(),
    val allFields: Map<String, String> = emptyMap()
)

sealed class StorageStats {
    object Error : StorageStats()

    data class Loaded(val total: Long, val free: Long) : StorageStats()
}

data class FlipperDeviceInfo(
    val deviceName: String? = null,
    val hardwareModel: String? = null,
    val hardwareRegion: String? = null,
    val hardwareRegionProv: String? = null,
    val hardwareVersion: String? = null,
    val hardwareOTPVersion: String? = null,
    val serialNumber: String? = null
)

data class FirmwareInfo(
    val softwareRevision: String? = null,
    val buildDate: String? = null,
    val target: String? = null,
    val protobufVersion: SemVer? = null,
    val deviceInfoVersion: SemVer? = null
)

data class RadioStackInfo(
    val type: RadioStackType? = null,
    val radioFirmware: String? = null
)

enum class RadioStackType(val id: String) {
    Full("1"), Light("3"), Beacon("4"),
    Basic("5"), FullExtAdv("6"), HCIExtAdv("7"), Unkwown("-1");

    companion object {
        fun find(radioType: String?): RadioStackType? {
            if (radioType == null) return null
            return values().associateBy(RadioStackType::id)[radioType] ?: Unkwown
        }
    }
}
