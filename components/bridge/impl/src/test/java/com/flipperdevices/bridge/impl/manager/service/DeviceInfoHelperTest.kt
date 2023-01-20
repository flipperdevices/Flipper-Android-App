package com.flipperdevices.bridge.impl.manager.service

import com.flipperdevices.bridge.api.model.FirmwareInfo
import com.flipperdevices.bridge.api.model.FlipperDeviceInfo
import com.flipperdevices.bridge.api.model.FlipperRpcInformation
import com.flipperdevices.bridge.api.model.RadioStackInfo
import com.flipperdevices.bridge.api.model.RadioStackType
import com.flipperdevices.bridge.impl.manager.service.requestservice.DeviceInfoHelper
import com.flipperdevices.bridge.impl.manager.service.requestservice.InternalFlipperRpcInformationRaw
import com.flipperdevices.core.data.SemVer
import kotlinx.collections.immutable.persistentMapOf
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class DeviceInfoHelperTest(
    private val fields: Map<String, String>,
    private val infoExpected: FlipperRpcInformation
) {
    @Test
    fun `Parse fields`() {
        val deviceInfo = DeviceInfoHelper.mapRawRpcInformation(
            InternalFlipperRpcInformationRaw(
                otherFields = fields
            )
        )
        Assert.assertEquals(infoExpected.flipperDeviceInfo, deviceInfo.flipperDeviceInfo)
        Assert.assertEquals(infoExpected.firmware, deviceInfo.firmware)
        Assert.assertEquals(infoExpected.radioStack, deviceInfo.radioStack)
        Assert.assertEquals(infoExpected.otherFields, deviceInfo.otherFields)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        @Suppress("LongMethod")
        fun data() = listOf(
            arrayOf(
                mapOf(
                    "hardware_name" to "My Flipper",
                    "hardware_model" to "Flipper Zero",
                    "hardware_region" to "1",
                    "hardware_region_provisioned" to "UA",
                    "hardware_ver" to "12",
                    "hardware_otp_ver" to "2",
                    "hardware_uid" to "ABC456",
                    "firmware_commit" to "dev",
                    "firmware_branch" to "dev12345",
                    "firmware_build_date" to "2077-98-11",
                    "firmware_target" to "7",
                    "protobuf_version_major" to "0",
                    "protobuf_version_minor" to "1",
                    "radio_stack_major" to "3",
                    "radio_stack_minor" to "5",
                    "radio_stack_type" to "7",
                    "other_fields_1" to "1",
                    "other_fields_2" to "2"
                ),
                FlipperRpcInformation(
                    flipperDeviceInfo = FlipperDeviceInfo(
                        deviceName = "My Flipper",
                        hardwareModel = "Flipper Zero",
                        hardwareRegion = "1",
                        hardwareRegionProv = "UA",
                        hardwareVersion = "12",
                        hardwareOTPVersion = "2",
                        serialNumber = "ABC456"
                    ),
                    firmware = FirmwareInfo(
                        softwareRevision = "Dev12345 dev",
                        buildDate = "2077-98-11",
                        target = "7",
                        protobufVersion = SemVer(0, 1)
                    ),
                    radioStack = RadioStackInfo(
                        type = RadioStackType.HCIExtAdv,
                        radioFirmware = "3.5.7"
                    ),
                    otherFields = persistentMapOf(
                        "other_fields_1" to "1",
                        "other_fields_2" to "2"
                    )
                )
            ),
            arrayOf(
                mapOf(
                    "hardware_model" to "Flipper Zero",
                    "hardware_region" to "1",
                    "hardware_region_provisioned" to "UA",
                    "hardware_ver" to "12",
                    "hardware_otp_ver" to "2",
                    "hardware_uid" to "ABC456",
                    "firmware_commit" to "1234",
                    "firmware_branch" to "unknown",
                    "firmware_build_date" to "2077-98-11",
                    "firmware_target" to "7",
                    "protobuf_version_minor" to "1",
                    "radio_stack_major" to "3",
                    "radio_stack_minor" to "5",
                    "radio_stack_type" to "4",
                    "other_fields_1" to "1"
                ),
                FlipperRpcInformation(
                    flipperDeviceInfo = FlipperDeviceInfo(
                        deviceName = null,
                        hardwareModel = "Flipper Zero",
                        hardwareRegion = "1",
                        hardwareRegionProv = "UA",
                        hardwareVersion = "12",
                        hardwareOTPVersion = "2",
                        serialNumber = "ABC456"
                    ),
                    firmware = FirmwareInfo(
                        softwareRevision = "Unknown 1234",
                        buildDate = "2077-98-11",
                        target = "7",
                        protobufVersion = null
                    ),
                    radioStack = RadioStackInfo(
                        type = RadioStackType.Beacon,
                        radioFirmware = "3.5.4"
                    ),
                    otherFields = persistentMapOf(
                        "other_fields_1" to "1"
                    )
                )
            )
        )
    }
}
