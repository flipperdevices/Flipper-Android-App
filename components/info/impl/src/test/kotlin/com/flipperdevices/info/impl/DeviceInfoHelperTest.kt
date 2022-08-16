package com.flipperdevices.info.impl

import com.flipperdevices.info.impl.fragment.DeviceInfoHelper
import com.flipperdevices.info.impl.model.FirmwareInfo
import com.flipperdevices.info.impl.model.FlipperDeviceInfo
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
    }

    @Test
    fun `Parse empty fields`() {
        val copyFields = fields.toMutableMap()
        copyFields.apply {
            replace("radio_stack_minor", "4")
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
