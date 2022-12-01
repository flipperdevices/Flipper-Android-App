package com.flipperdevices.bridge.impl.manager.service.requestservice

import com.flipperdevices.bridge.api.model.FirmwareInfo
import com.flipperdevices.bridge.api.model.FlipperDeviceInfo
import com.flipperdevices.bridge.api.model.FlipperRpcInformation
import com.flipperdevices.bridge.api.model.RadioStackInfo
import com.flipperdevices.bridge.api.model.RadioStackType
import com.flipperdevices.bridge.api.model.StorageStats
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.ktx.jre.isNotNull
import com.flipperdevices.core.ktx.jre.titlecaseFirstCharIfItIsLowercase

private const val DEVICE_NAME = "hardware_name"
private const val HARDWARE_MODEL = "hardware_model"
private const val HARDWARE_REGION = "hardware_region"
private const val HARDWARE_REGION_PROV = "hardware_region_provisioned"
private const val HARDWARE_VERSION = "hardware_ver"
private const val HARDWARE_OTP_VERSION = "hardware_otp_ver"
private const val SERIAL_NUMBER = "hardware_uid"

private const val FIRMWARE_COMMIT = "firmware_commit"
private const val FIRMWARE_BRANCH = "firmware_branch"
private const val FIRMWARE_BUILD_DATE = "firmware_build_date"
private const val FIRMWARE_TARGET = "firmware_target"
private const val PROTOBUF_MAJOR = "protobuf_version_major"
private const val PROTOBUF_MINOR = "protobuf_version_minor"
private const val DEVICE_INFO_MAJOR = "device_info_major"
private const val DEVICE_INFO_MINOR = "device_info_minor"

private const val RADIO_STACK_MAJOR = "radio_stack_major"
private const val RADIO_STACK_MINOR = "radio_stack_minor"
private const val RADIO_STACK_TYPE = "radio_stack_type"

// This fields uses in NOT other section
private val usedFields = setOf(
    DEVICE_NAME, HARDWARE_MODEL, HARDWARE_REGION, HARDWARE_REGION_PROV,
    HARDWARE_VERSION, HARDWARE_OTP_VERSION, SERIAL_NUMBER,
    FIRMWARE_COMMIT, FIRMWARE_BRANCH, FIRMWARE_BUILD_DATE,
    FIRMWARE_TARGET, PROTOBUF_MAJOR, PROTOBUF_MINOR,
    RADIO_STACK_MAJOR, RADIO_STACK_MINOR, RADIO_STACK_TYPE,
    DEVICE_INFO_MAJOR, DEVICE_INFO_MINOR
)

internal data class InternalFlipperRpcInformationRaw(
    val internalStorageStats: StorageStats? = null,
    val externalStorageStats: StorageStats? = null,
    val otherFields: Map<String, String> = emptyMap()
)

internal object DeviceInfoHelper {
    fun mapRawRpcInformation(
        rawInformation: InternalFlipperRpcInformationRaw
    ): FlipperRpcInformation {
        val fields = rawInformation.otherFields
        val flipperDeviceInfo = FlipperDeviceInfo(
            deviceName = fields[DEVICE_NAME],
            hardwareModel = fields[HARDWARE_MODEL],
            hardwareRegion = fields[HARDWARE_REGION],
            hardwareRegionProv = fields[HARDWARE_REGION_PROV],
            hardwareVersion = fields[HARDWARE_VERSION],
            hardwareOTPVersion = fields[HARDWARE_OTP_VERSION],
            serialNumber = fields[SERIAL_NUMBER]
        )

        val firmwareCommit = fields[FIRMWARE_COMMIT]
        val firmwareBranch = fields[FIRMWARE_BRANCH]
        val softwareRevision = softwareRevision(firmwareCommit, firmwareBranch)

        val protobufMajor = fields[PROTOBUF_MAJOR]
        val protobufMinor = fields[PROTOBUF_MINOR]
        val protobufVersion = protobufVersion(protobufMajor, protobufMinor)

        val deviceInfoMajor = fields[DEVICE_INFO_MAJOR]
        val deviceInfoMinor = fields[DEVICE_INFO_MINOR]
        val deviceInfoVersion = deviceInfoVersion(deviceInfoMajor, deviceInfoMinor)

        val firmwareInfo = FirmwareInfo(
            softwareRevision = softwareRevision,
            buildDate = fields[FIRMWARE_BUILD_DATE],
            target = fields[FIRMWARE_TARGET],
            protobufVersion = protobufVersion,
            deviceInfoVersion = deviceInfoVersion
        )

        val radioMajor = fields[RADIO_STACK_MAJOR]
        val radioMinor = fields[RADIO_STACK_MINOR]
        val radioType = fields[RADIO_STACK_TYPE]

        val radioStackInfo = RadioStackInfo(
            type = radioType(radioType),
            radioFirmware = radioFirmware(radioMajor, radioMinor, radioType)
        )

        return FlipperRpcInformation(
            internalStorageStats = rawInformation.internalStorageStats,
            externalStorageStats = rawInformation.externalStorageStats,
            flipperDeviceInfo = flipperDeviceInfo,
            firmware = firmwareInfo,
            radioStack = radioStackInfo,
            otherFields = fields.minus(usedFields),
            allFields = fields
        )
    }

    // softwareRevision Branch.Commit
    private fun softwareRevision(firmwareCommit: String?, firmwareBranch: String?): String? {
        return if (isNotNull(firmwareCommit, firmwareBranch)) {
            val firmwareBranchCapitalize = firmwareBranch?.titlecaseFirstCharIfItIsLowercase()
            "$firmwareBranchCapitalize $firmwareCommit"
        } else null
    }

    // protobuf Major.Minor
    private fun protobufVersion(protobufMajor: String?, protobufMinor: String?): SemVer? {
        return if (isNotNull(protobufMajor, protobufMinor)) SemVer(
            protobufMajor?.toIntOrNull() ?: 0,
            protobufMinor?.toIntOrNull() ?: 0
        )
        else null
    }

    // deviceInfo Major.Minor
    private fun deviceInfoVersion(deviceInfoMajor: String?, deviceInfoMinor: String?): SemVer? {
        return if (isNotNull(deviceInfoMajor, deviceInfoMinor)) SemVer(
            deviceInfoMajor?.toIntOrNull() ?: 0,
            deviceInfoMinor?.toIntOrNull() ?: 0
        )
        else null
    }

    // radio Major.Minor.Type
    private fun radioFirmware(
        radioMajor: String?,
        radioMinor: String?,
        radioType: String?
    ): String? {
        return if (isNotNull(radioMajor, radioMinor, radioType)) {
            "$radioMajor.$radioMinor.$radioType"
        } else null
    }

    private fun radioType(radioType: String?): RadioStackType? {
        return RadioStackType.find(radioType)
    }
}
