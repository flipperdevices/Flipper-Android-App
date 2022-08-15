package com.flipperdevices.info.impl.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.core.ui.theme.LocalPallet

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
    val firmwareCommit: String?,
    val softwareRevision: String?,
    val buildDate: String?,
    val target: String?,
    val protobufVersion: String?
) {
    @Composable
    fun getFirmwareColor(): Color {
        if (firmwareCommit == null) return Color.Transparent
        if (Constants.FirmwareCommit.DEV == firmwareCommit) {
            return LocalPallet.current.channelFirmwareDev
        }
        if (Constants.FirmwareCommit.RELEASE_REGEX.toRegex() matches firmwareCommit) {
            return LocalPallet.current.channelFirmwareRelease
        }
        if (Constants.FirmwareCommit.RC_REGEX.toRegex() matches firmwareCommit) {
            return LocalPallet.current.channelFirmwareReleaseCandidate
        }

        return LocalPallet.current.channelFirmwareUnknown
    }
}

data class RadioStackInfo(
    val radioFirmware: String?
)

data class OtherInfo(
    val fields: Set<Map.Entry<String, String>> = setOf()
)
