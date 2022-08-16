package com.flipperdevices.info.impl

import com.flipperdevices.info.impl.fragment.DeviceInfoHelper
import com.flipperdevices.info.impl.model.FirmwareInfo
import com.flipperdevices.info.impl.model.FlipperDeviceInfo
import com.flipperdevices.info.impl.model.OtherInfo
import com.flipperdevices.info.impl.model.RadioStackInfo
import com.flipperdevices.info.impl.model.RadioStackType
import com.flipperdevices.updater.api.FirmwareVersionBuilderApi
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class DeviceInfoHelperTest {

    private lateinit var firmwareVersionBuilderApi: FirmwareVersionBuilderApi

    @Before
    fun setup() {
        firmwareVersionBuilderApi = mock()
    }

    private val fields = mapOf(
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
    )

    private val flipperDeviceInfo = FlipperDeviceInfo(
        deviceName = "My Flipper",
        hardwareModel = "Flipper Zero",
        hardwareRegion = "1",
        hardwareRegionProv = "UA",
        hardwareVersion = "12",
        hardwareOTPVersion = "2",
        serialNumber = "ABC456"
    )

    private val firmwareInfo = FirmwareInfo(
        firmwareChannel = null,
        softwareRevision = "Dev12345 dev",
        buildDate = "2077-98-11",
        target = "7",
        protobufVersion = "0.1"
    )

    private val radioStackInfo = RadioStackInfo(
        type = RadioStackType.HCIExtAdv,
        radioFirmware = "3.5.7"
    )

    private val otherFields = OtherInfo(
        fields = fields.entries.filter { it.key.startsWith("other_fields") }.toSet()
    )

    @Test
    fun `Parse all fields`() {
        val deviceInfo = DeviceInfoHelper.parseFields(
            fields = fields,
            firmwareChannel = {
                firmwareVersionBuilderApi.getFirmwareChannel(
                    commit = it ?: throw NullPointerException("")
                )
            }
        )
        Assert.assertEquals(flipperDeviceInfo, deviceInfo.flipperDevices)
        Assert.assertEquals(firmwareInfo, deviceInfo.firmware)
        Assert.assertEquals(radioStackInfo, deviceInfo.radioStack)
        Assert.assertEquals(otherFields, deviceInfo.other)
    }

    @Test
    fun `Parse empty fields`() {
        val copyFields = fields.toMutableMap()
        copyFields.apply {
            remove("hardware_name")
            remove("firmware_branch")
        }
        val deviceInfo = DeviceInfoHelper.parseFields(
            fields = copyFields,
            firmwareChannel = {
                firmwareVersionBuilderApi.getFirmwareChannel(
                    commit = it ?: throw NullPointerException("")
                )
            }
        )
        Assert.assertEquals(flipperDeviceInfo.copy(deviceName = null), deviceInfo.flipperDevices)
        Assert.assertEquals(firmwareInfo.copy(softwareRevision = null), deviceInfo.firmware)
    }
}
