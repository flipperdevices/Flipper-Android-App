package com.flipperdevices.bridge.connection.feature.rpcstats.impl.shaketoreport

import com.flipperdevices.bridge.connection.feature.storageinfo.model.FlipperStorageInformation
import com.flipperdevices.bridge.connection.feature.storageinfo.model.StorageStats
import com.flipperdevices.bridge.connection.feature.storageinfo.model.dataOrNull

private const val FLIPPER_RPC_INFO_PREFIX = "flipper_rpc_info"
private const val FLIPPER_RPC_INFO_INT_TOTAL = "${FLIPPER_RPC_INFO_PREFIX}_int_total"
private const val FLIPPER_RPC_INFO_INT_FREE = "${FLIPPER_RPC_INFO_PREFIX}_int_free"
private const val FLIPPER_RPC_INFO_EXT_TOTAL = "${FLIPPER_RPC_INFO_PREFIX}_ext_total"
private const val FLIPPER_RPC_INFO_EXT_FREE = "${FLIPPER_RPC_INFO_PREFIX}_ext_free"

object FlipperInformationMapping {
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
