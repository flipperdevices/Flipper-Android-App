package com.flipperdevices.bridge.impl.utils

import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.shake2report.api.Shake2ReportApi

object Shake2ReportHelper {
    fun updateGattInformation(
        shake2ReportApi: Shake2ReportApi,
        gattInformation: FlipperGATTInformation
    ) {
        shake2ReportApi.setExtra(convert(gattInformation))
    }

    private const val FLIPPER_DEVICE_NAME = "flipper_device_name"
    private const val FLIPPER_MANUFACTURER_NAME = "flipper_manufacturer_name"
    private const val FLIPPER_HARDWARE_REVISION = "flipper_hardware_revision"
    private const val FLIPPER_SOFTWARE_REVISION = "flipper_software_revision"
    private const val FLIPPER_BATTERY_LEVEL = "flipper_battery_level"
    private fun convert(gattInformation: FlipperGATTInformation): List<Pair<String, String>> {
        val tagsList = mutableListOf<Pair<String, String>>()
        gattInformation.deviceName?.let {
            tagsList.add(FLIPPER_DEVICE_NAME to it)
        }
        gattInformation.manufacturerName?.let {
            tagsList.add(FLIPPER_MANUFACTURER_NAME to it)
        }
        gattInformation.hardwareRevision?.let {
            tagsList.add(FLIPPER_HARDWARE_REVISION to it)
        }
        gattInformation.softwareVersion?.let {
            tagsList.add(FLIPPER_SOFTWARE_REVISION to it)
        }
        gattInformation.batteryLevel?.let {
            tagsList.add(FLIPPER_BATTERY_LEVEL to it.toString())
        }
        return tagsList
    }
}
