package com.flipperdevices.info.impl

import com.flipperdevices.info.impl.fragment.DeviceInfoHelper
import com.flipperdevices.info.impl.model.DeviceFullInfo
import com.flipperdevices.info.impl.model.FirmwareInfo
import com.flipperdevices.info.impl.model.FlipperDeviceInfo
import com.flipperdevices.info.impl.model.OtherInfo
import com.flipperdevices.info.impl.model.RadioStackInfo
import com.flipperdevices.info.impl.model.RadioStackType
import com.flipperdevices.updater.api.FirmwareVersionBuilderApi
import com.flipperdevices.updater.model.FirmwareChannel
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(Parameterized::class)
class DeviceInfoHelperTest(
    private val fields: Map<String, String>,
    private val firmwareChannel: FirmwareChannel,
    private val info: DeviceFullInfo
) {

    private lateinit var firmwareVersionBuilderApi: FirmwareVersionBuilderApi

    @Before
    fun setup() {
        firmwareVersionBuilderApi = mock()
    }

    @Test
    fun `Parse fields`() {
        whenever(firmwareVersionBuilderApi.getFirmwareChannel(any())).doReturn(firmwareChannel)
        val deviceInfo = DeviceInfoHelper.parseFields(
            fields = fields,
            firmwareChannel = {
                firmwareVersionBuilderApi.getFirmwareChannel(
                    branch = it ?: throw NullPointerException("")
                )
            }
        )
        Assert.assertEquals(info.flipperDevices, deviceInfo.flipperDevices)
        Assert.assertEquals(info.firmware, deviceInfo.firmware)
        Assert.assertEquals(info.radioStack, deviceInfo.radioStack)
        Assert.assertEquals(info.other, deviceInfo.other)
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
                    "other_fields_2" to "2",
                ),
                FirmwareChannel.DEV,
                DeviceFullInfo(
                    flipperDevices = FlipperDeviceInfo(
                        deviceName = "My Flipper",
                        hardwareModel = "Flipper Zero",
                        hardwareRegion = "1",
                        hardwareRegionProv = "UA",
                        hardwareVersion = "12",
                        hardwareOTPVersion = "2",
                        serialNumber = "ABC456"
                    ),
                    firmware = FirmwareInfo(
                        firmwareChannel = FirmwareChannel.DEV,
                        softwareRevision = "Dev12345 dev",
                        buildDate = "2077-98-11",
                        target = "7",
                        protobufVersion = "0.1"
                    ),
                    radioStack = RadioStackInfo(
                        type = RadioStackType.HCIExtAdv,
                        radioFirmware = "3.5.7"
                    ),
                    other = OtherInfo(
                        fields = mapOf(
                            "other_fields_1" to "1",
                            "other_fields_2" to "2",
                        ).entries
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
                    "other_fields_1" to "1",
                ),
                FirmwareChannel.UNKNOWN,
                DeviceFullInfo(
                    flipperDevices = FlipperDeviceInfo(
                        deviceName = null,
                        hardwareModel = "Flipper Zero",
                        hardwareRegion = "1",
                        hardwareRegionProv = "UA",
                        hardwareVersion = "12",
                        hardwareOTPVersion = "2",
                        serialNumber = "ABC456"
                    ),
                    firmware = FirmwareInfo(
                        firmwareChannel = FirmwareChannel.UNKNOWN,
                        softwareRevision = "Unknown 1234",
                        buildDate = "2077-98-11",
                        target = "7",
                        protobufVersion = null
                    ),
                    radioStack = RadioStackInfo(
                        type = RadioStackType.Beacon,
                        radioFirmware = "3.5.4"
                    ),
                    other = OtherInfo(
                        fields = mapOf(
                            "other_fields_1" to "1",
                        ).entries
                    )
                )
            )
        )
    }
}
