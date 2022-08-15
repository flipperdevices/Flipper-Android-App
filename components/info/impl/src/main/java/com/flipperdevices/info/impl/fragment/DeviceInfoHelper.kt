package com.flipperdevices.info.impl.fragment

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ktx.jre.isNotNull
import com.flipperdevices.core.ktx.jre.titlecaseFirstCharIfItIsLowercase
import com.flipperdevices.info.impl.model.DeviceFullInfo
import com.flipperdevices.info.impl.model.FirmwareInfo
import com.flipperdevices.info.impl.model.FlipperDeviceInfo
import com.flipperdevices.info.impl.model.OtherInfo
import com.flipperdevices.info.impl.model.RadioStackInfo
import com.flipperdevices.info.impl.model.RadioStackType
import com.flipperdevices.updater.model.FirmwareChannel

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

private const val RADIO_STACK_MAJOR = "radio_stack_major"
private const val RADIO_STACK_MINOR = "radio_stack_minor"
private const val RADIO_STACK_TYPE = "radio_stack_type"

// This fields uses in NOT other section
private val usedFields = listOf(
    DEVICE_NAME, HARDWARE_MODEL, HARDWARE_REGION, HARDWARE_REGION_PROV,
    HARDWARE_VERSION, HARDWARE_OTP_VERSION, SERIAL_NUMBER,
    FIRMWARE_COMMIT, FIRMWARE_BRANCH, FIRMWARE_BUILD_DATE,
    FIRMWARE_TARGET, PROTOBUF_MAJOR, PROTOBUF_MINOR,
    RADIO_STACK_MAJOR, RADIO_STACK_MINOR, RADIO_STACK_TYPE
)

object DeviceInfoHelper {
    @Composable
    fun parseFields(
        fields: Map<String, String>,
        firmwareChannel: (String?) -> FirmwareChannel?
    ): DeviceFullInfo {
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

        val firmwareInfo = FirmwareInfo(
            firmwareChannel = firmwareChannel(firmwareCommit),
            softwareRevision = softwareRevision,
            buildDate = fields[FIRMWARE_BUILD_DATE],
            target = fields[FIRMWARE_TARGET],
            protobufVersion = protobufVersion
        )

        val radioMajor = fields[RADIO_STACK_MAJOR]
        val radioMinor = fields[RADIO_STACK_MINOR]
        val radioType = fields[RADIO_STACK_TYPE]

        val radioStackInfo = RadioStackInfo(
            type = radioType(radioType),
            radioFirmware = radioFirmware(radioMajor, radioMinor, radioType)
        )

        val otherInfo = OtherInfo(
            fields = fields.filterNot { usedFields.contains(it.key) }.entries
        )

        return DeviceFullInfo(
            flipperDeviceInfo,
            firmwareInfo,
            radioStackInfo,
            otherInfo
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
    private fun protobufVersion(protobufMajor: String?, protobufMinor: String?): String? {
        return if (isNotNull(protobufMajor, protobufMinor)) "$protobufMajor.$protobufMinor"
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
        return when (radioType) {
            "1" -> RadioStackType.Full
            "3" -> RadioStackType.Light
            "4" -> RadioStackType.Beacon
            "5" -> RadioStackType.Basic
            "6" -> RadioStackType.FullExtAdv
            "7" -> RadioStackType.HCIExtAdv
            null -> null
            else -> RadioStackType.Unkwown
        }
    }
}
