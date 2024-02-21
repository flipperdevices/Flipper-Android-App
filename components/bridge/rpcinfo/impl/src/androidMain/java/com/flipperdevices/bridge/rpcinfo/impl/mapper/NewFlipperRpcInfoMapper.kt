package com.flipperdevices.bridge.rpcinfo.impl.mapper

import com.flipperdevices.bridge.rpcinfo.model.FirmwareInfo
import com.flipperdevices.bridge.rpcinfo.model.FlipperDeviceInfo
import com.flipperdevices.bridge.rpcinfo.model.FlipperRpcInformation
import com.flipperdevices.bridge.rpcinfo.model.RadioStackInfo
import kotlinx.collections.immutable.toImmutableMap

private const val DEVICE_NAME = "devinfo_hardware.name"
private const val HARDWARE_MODEL = "devinfo_hardware.model"
private const val HARDWARE_REGION = "devinfo_hardware.region.builtin"
private const val HARDWARE_REGION_PROV = "devinfo_hardware.region.provisioned"
private const val HARDWARE_VERSION = "devinfo_hardware.ver"
private const val HARDWARE_OTP_VERSION = "devinfo_hardware.otp.ver"
private const val SERIAL_NUMBER = "devinfo_hardware.uid"

private const val FIRMWARE_COMMIT = "devinfo_firmware.commit.hash"
private const val FIRMWARE_BRANCH = "devinfo_firmware.branch.name"
private const val FIRMWARE_BUILD_DATE = "devinfo_firmware.build.date"
private const val FIRMWARE_TARGET = "devinfo_firmware.target"
private const val PROTOBUF_MAJOR = "devinfo_protobuf.version.major"
private const val PROTOBUF_MINOR = "devinfo_protobuf.version.minor"
private const val DEVICE_INFO_MAJOR = "devinfo_format.major"
private const val DEVICE_INFO_MINOR = "devinfo_format.minor"

private const val RADIO_STACK_MAJOR = "devinfo_radio.stack.major"
private const val RADIO_STACK_MINOR = "devinfo_radio.stack.minor"
private const val RADIO_STACK_TYPE = "devinfo_radio.stack.type"
private const val RADIO_STACK_SUB = "devinfo_radio.stack.sub"

// This fields uses in NOT other section
private val usedFields = setOf(
    DEVICE_NAME, HARDWARE_MODEL, HARDWARE_REGION, HARDWARE_REGION_PROV,
    HARDWARE_VERSION, HARDWARE_OTP_VERSION, SERIAL_NUMBER,
    FIRMWARE_COMMIT, FIRMWARE_BRANCH, FIRMWARE_BUILD_DATE,
    FIRMWARE_TARGET, PROTOBUF_MAJOR, PROTOBUF_MINOR,
    RADIO_STACK_MAJOR, RADIO_STACK_MINOR, RADIO_STACK_TYPE, RADIO_STACK_SUB,
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
        val radioSub = fields[RADIO_STACK_SUB]

        val radioStackInfo = RadioStackInfo(
            type = RpcInformationInfoHelper.radioType(radioType),
            radioFirmware = RpcInformationInfoHelper.radioFirmware(
                radioMajor,
                radioMinor,
                radioSub
            )
        )

        val otherFields = mutableMapOf<String, String>()
        for ((key, value) in fields.minus(usedFields)) {
            val newKeyName = key.substringAfter('_')
            if (otherFields.containsKey(newKeyName)) {
                otherFields[key] = value
            } else {
                otherFields[newKeyName] = value
            }
        }

        return FlipperRpcInformation(
            flipperDeviceInfo = flipperDeviceInfo,
            firmware = firmwareInfo,
            radioStack = radioStackInfo,
            otherFields = otherFields.toImmutableMap(),
            allFields = fields.toImmutableMap()
        )
    }
}
