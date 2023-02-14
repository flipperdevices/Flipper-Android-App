package com.flipperdevices.analytics.shake2report.impl.helper

import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.info.api.model.FlipperRpcInformation
import com.flipperdevices.info.api.model.FlipperStorageInformation
import com.flipperdevices.info.api.model.StorageStats
import com.flipperdevices.info.api.model.dataOrNull

private const val FLIPPER_DEVICE_NAME = "flipper_device_name"
private const val FLIPPER_MANUFACTURER_NAME = "flipper_manufacturer_name"
private const val FLIPPER_HARDWARE_REVISION = "flipper_hardware_revision"
private const val FLIPPER_SOFTWARE_REVISION = "flipper_software_revision"
private const val FLIPPER_BATTERY_LEVEL = "flipper_battery_level"

private const val FLIPPER_RPC_INFO_PREFIX = "flipper_rpc_info"
private const val FLIPPER_RPC_INFO_INT_TOTAL = "${FLIPPER_RPC_INFO_PREFIX}_int_total"
private const val FLIPPER_RPC_INFO_INT_FREE = "${FLIPPER_RPC_INFO_PREFIX}_int_free"
private const val FLIPPER_RPC_INFO_EXT_TOTAL = "${FLIPPER_RPC_INFO_PREFIX}_ext_total"
private const val FLIPPER_RPC_INFO_EXT_FREE = "${FLIPPER_RPC_INFO_PREFIX}_ext_free"

object FlipperInformationMapping {
    fun convert(gattInformation: FlipperGATTInformation): List<Pair<String, String>> {
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

    fun convert(gattInformation: FlipperRpcInformation): List<Pair<String, String>> {
        val tagsList = mutableListOf<Pair<String, String>>()
        gattInformation.allFields.forEach { (key, value) ->
            tagsList.add("${FLIPPER_RPC_INFO_PREFIX}_$key" to value)
        }
        return tagsList
    }

    fun convert(storageInfo: FlipperStorageInformation): List<Pair<String, String>> {
        val tagsList = mutableListOf<Pair<String, String>>()
        storageInfo.externalStorageStatus.dataOrNull()?.let { stats ->
            if (stats is StorageStats.Loaded) {
                tagsList.add(FLIPPER_RPC_INFO_EXT_TOTAL to stats.total.toString())
                tagsList.add(FLIPPER_RPC_INFO_EXT_FREE to stats.free.toString())
            }
        }
        storageInfo.internalStorageStatus.dataOrNull()?.let { stats ->
            if (stats is StorageStats.Loaded) {
                tagsList.add(FLIPPER_RPC_INFO_INT_TOTAL to stats.total.toString())
                tagsList.add(FLIPPER_RPC_INFO_INT_FREE to stats.free.toString())
            }
        }
        return tagsList
    }
}
