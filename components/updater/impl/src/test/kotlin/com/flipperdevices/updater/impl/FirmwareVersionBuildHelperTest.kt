package com.flipperdevices.updater.impl

import com.flipperdevices.updater.impl.utils.FirmwareVersionBuildHelper
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import org.junit.Assert
import org.junit.Test

class FirmwareVersionBuildHelperTest {

    @Test
    fun `Correct dev firmware`() {
        val firmwareVersion = "3b81940f dev 2442 16-07-2022"
        val parseFirmware =
            FirmwareVersionBuildHelper.buildFirmwareVersionFromString(firmwareVersion)
        val actualFirmware = FirmwareVersion(
            channel = FirmwareChannel.DEV,
            version = "3b81940f",
            buildDate = "16-07-2022"
        )
        Assert.assertEquals(parseFirmware, actualFirmware)
    }

    @Test
    fun `Correct release candidate firmware`() {
        val firmwareVersion = "2a1d679g 0.63.1-rc 1363 15-07-2022"
        val parseFirmware =
            FirmwareVersionBuildHelper.buildFirmwareVersionFromString(firmwareVersion)
        val actualFirmware = FirmwareVersion(
            channel = FirmwareChannel.RELEASE_CANDIDATE,
            version = "0.63.1",
            buildDate = "15-07-2022"
        )
        Assert.assertEquals(parseFirmware, actualFirmware)
    }

    @Test
    fun `Correct release firmware`() {
        val firmwareVersion = "3e5d499b 0.62.1 1363 13-07-2022"
        val parseFirmware =
            FirmwareVersionBuildHelper.buildFirmwareVersionFromString(firmwareVersion)
        val actualFirmware = FirmwareVersion(
            channel = FirmwareChannel.RELEASE,
            version = "0.62.1",
            buildDate = "13-07-2022"
        )
        Assert.assertEquals(parseFirmware, actualFirmware)
    }

    @Test
    fun `Correct unknown firmware`() {
        val firmwareVersion = "3e5d499b name/2204_bt_forget_devices02d45365 1363 13-07-2022"
        val parseFirmware =
            FirmwareVersionBuildHelper.buildFirmwareVersionFromString(firmwareVersion)
        val actualFirmware = FirmwareVersion(
            channel = FirmwareChannel.UNKNOWN,
            version = "name/2204_bt_forget_devices02d45365",
            buildDate = "13-07-2022"
        )
        Assert.assertEquals(parseFirmware, actualFirmware)
    }

    @Test
    fun `Uncorrected device version part count`() {
        val firmwareVersion = "3e5d499b12345671363 13-07-2022"
        val parseFirmware =
            FirmwareVersionBuildHelper.buildFirmwareVersionFromString(firmwareVersion)
        Assert.assertEquals(parseFirmware, null)
    }
}
