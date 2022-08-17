package com.flipperdevices.info.impl.model

import com.flipperdevices.updater.model.FirmwareChannel

data class DeviceFullInfo(
    val flipperDevices: FlipperDeviceInfo,
    val firmware: FirmwareInfo,
    val radioStack: RadioStackInfo,
    val other: OtherInfo
)

data class FlipperDeviceInfo(
    val deviceName: String?,
    val hardwareModel: String?,
    val hardwareRegion: String?,
    val hardwareRegionProv: String?,
    val hardwareVersion: String?,
    val hardwareOTPVersion: String?,
    val serialNumber: String?
)

data class FirmwareInfo(
    val firmwareChannel: FirmwareChannel?,
    val softwareRevision: String?,
    val buildDate: String?,
    val target: String?,
    val protobufVersion: String?
)

data class RadioStackInfo(
    val type: RadioStackType?,
    val radioFirmware: String?
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

data class OtherInfo(
    val fields: Set<Map.Entry<String, String>> = setOf()
)
