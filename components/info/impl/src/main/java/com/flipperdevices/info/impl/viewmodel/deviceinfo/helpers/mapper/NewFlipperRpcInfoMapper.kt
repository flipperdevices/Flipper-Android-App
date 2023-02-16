package com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers.mapper

import com.flipperdevices.info.api.model.FirmwareInfo
import com.flipperdevices.info.api.model.FlipperDeviceInfo
import com.flipperdevices.info.api.model.FlipperRpcInformation
import com.flipperdevices.info.api.model.RadioStackInfo
import kotlinx.collections.immutable.toImmutableMap

private const val DEVICE_NAME = "hardware.name"
private const val HARDWARE_MODEL = "hardware.model"
private const val HARDWARE_REGION = "hardware.region.builtin"
private const val HARDWARE_REGION_PROV = "hardware.region.provisioned"
private const val HARDWARE_VERSION = "hardware.ver"
private const val HARDWARE_OTP_VERSION = "hardware.otp.ver"
private const val SERIAL_NUMBER = "hardware.uid"

private const val FIRMWARE_COMMIT = "firmware.commit.hash"
private const val FIRMWARE_BRANCH = "firmware.branch.name"
private const val FIRMWARE_BUILD_DATE = "firmware.build.date"
private const val FIRMWARE_TARGET = "firmware.target"
private const val PROTOBUF_MAJOR = "protobuf.version.major"
private const val PROTOBUF_MINOR = "protobuf.version.minor"
private const val DEVICE_INFO_MAJOR = "format.major"
private const val DEVICE_INFO_MINOR = "format.minor"

private const val RADIO_STACK_MAJOR = "radio.stack.major"
private const val RADIO_STACK_MINOR = "radio.stack.minor"
private const val RADIO_STACK_TYPE = "radio.stack.type"

// This fields uses in NOT other section
private val usedFields = setOf(
    DEVICE_NAME, HARDWARE_MODEL, HARDWARE_REGION, HARDWARE_REGION_PROV,
    HARDWARE_VERSION, HARDWARE_OTP_VERSION, SERIAL_NUMBER,
    FIRMWARE_COMMIT, FIRMWARE_BRANCH, FIRMWARE_BUILD_DATE,
    FIRMWARE_TARGET, PROTOBUF_MAJOR, PROTOBUF_MINOR,
    RADIO_STACK_MAJOR, RADIO_STACK_MINOR, RADIO_STACK_TYPE,
    DEVICE_INFO_MAJOR, DEVICE_INFO_MINOR
)

internal class NewFlipperRpcInfoMapper : FlipperRpcInfoMapper {
    override fun map(
        raw: InternalFlipperRpcInformationRaw
    ): FlipperRpcInformation {
        val fields = raw.otherFields
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
        val softwareRevision =
            RpcInformationInfoHelper.softwareRevision(firmwareCommit, firmwareBranch)

        val protobufMajor = fields[PROTOBUF_MAJOR]
        val protobufMinor = fields[PROTOBUF_MINOR]
        val protobufVersion = RpcInformationInfoHelper.protobufVersion(protobufMajor, protobufMinor)

        val deviceInfoMajor = fields[DEVICE_INFO_MAJOR]
        val deviceInfoMinor = fields[DEVICE_INFO_MINOR]
        val deviceInfoVersion =
            RpcInformationInfoHelper.deviceInfoVersion(deviceInfoMajor, deviceInfoMinor)

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
            type = RpcInformationInfoHelper.radioType(radioType),
            radioFirmware = RpcInformationInfoHelper.radioFirmware(
                radioMajor,
                radioMinor,
                radioType
            )
        )

        return FlipperRpcInformation(
            flipperDeviceInfo = flipperDeviceInfo,
            firmware = firmwareInfo,
            radioStack = radioStackInfo,
            otherFields = fields.minus(usedFields).toImmutableMap(),
            allFields = fields.toImmutableMap()
        )
    }
}
