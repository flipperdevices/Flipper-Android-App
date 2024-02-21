package com.flipperdevices.bridge.rpcinfo.impl.mapper

import com.flipperdevices.bridge.rpcinfo.impl.mapper.RpcInformationInfoHelper.deviceInfoVersion
import com.flipperdevices.bridge.rpcinfo.impl.mapper.RpcInformationInfoHelper.protobufVersion
import com.flipperdevices.bridge.rpcinfo.impl.mapper.RpcInformationInfoHelper.radioFirmware
import com.flipperdevices.bridge.rpcinfo.impl.mapper.RpcInformationInfoHelper.radioType
import com.flipperdevices.bridge.rpcinfo.impl.mapper.RpcInformationInfoHelper.softwareRevision
import com.flipperdevices.bridge.rpcinfo.model.FirmwareInfo
import com.flipperdevices.bridge.rpcinfo.model.FlipperDeviceInfo
import com.flipperdevices.bridge.rpcinfo.model.FlipperRpcInformation
import com.flipperdevices.bridge.rpcinfo.model.RadioStackInfo
import kotlinx.collections.immutable.toImmutableMap

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
private const val RADIO_STACK_SUB = "radio_stack_sub"

// This fields uses in NOT other section
private val usedFields = setOf(
    DEVICE_NAME, HARDWARE_MODEL, HARDWARE_REGION, HARDWARE_REGION_PROV,
    HARDWARE_VERSION, HARDWARE_OTP_VERSION, SERIAL_NUMBER,
    FIRMWARE_COMMIT, FIRMWARE_BRANCH, FIRMWARE_BUILD_DATE,
    FIRMWARE_TARGET, PROTOBUF_MAJOR, PROTOBUF_MINOR,
    RADIO_STACK_MAJOR, RADIO_STACK_MINOR, RADIO_STACK_TYPE, RADIO_STACK_SUB,
    DEVICE_INFO_MAJOR, DEVICE_INFO_MINOR
)

internal class DeprecatedFlipperRpcInfoMapper : FlipperRpcInfoMapper {
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
        val radioSub = fields[RADIO_STACK_SUB]

        val radioStackInfo = RadioStackInfo(
            type = radioType(radioType),
            radioFirmware = radioFirmware(radioMajor, radioMinor, radioSub)
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
