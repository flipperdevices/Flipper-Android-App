@file: Suppress("NoWildcardImports")

package com.flipperdevices.bridge.connection.feature.rpcinfo.impl.mapper

import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiProperty
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiProperty.DeviceInfo.*
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FirmwareInfo
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperDeviceInfo
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperRpcInformation
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.RadioStackInfo
import kotlinx.collections.immutable.toImmutableMap

// This fields uses in NOT other section
private val usedFields = setOf(
    DEVICE_NAME, HARDWARE_MODEL, HARDWARE_REGION, HARDWARE_REGION_PROV, HARDWARE_VERSION,
    HARDWARE_OTP_VERSION, SERIAL_NUMBER, FIRMWARE_COMMIT, FIRMWARE_BRANCH, FIRMWARE_BUILD_DATE,
    FIRMWARE_TARGET, PROTOBUF_MAJOR, PROTOBUF_MINOR, RADIO_STACK_MAJOR, RADIO_STACK_MINOR,
    RADIO_STACK_TYPE, RADIO_STACK_SUB, DEVICE_INFO_MAJOR, DEVICE_INFO_MINOR
)

internal class NewFlipperRpcInfoMapper : FlipperRpcInfoMapper<FGetInfoApiProperty> {
    override fun map(
        raw: InternalFlipperRpcInformationRaw<FGetInfoApiProperty>
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
            val newKeyName = key.key
            if (otherFields.containsKey(newKeyName)) {
                otherFields[key.path] = value
            } else {
                otherFields[newKeyName] = value
            }
        }

        return FlipperRpcInformation(
            flipperDeviceInfo = flipperDeviceInfo,
            firmware = firmwareInfo,
            radioStack = radioStackInfo,
            otherFields = otherFields.toImmutableMap(),
            allFields = fields.mapKeys { it.key.path }.toImmutableMap()
        )
    }
}
