package com.flipperdevices.bridge.rpcinfo.impl.shaketoreport

import com.flipperdevices.bridge.rpcinfo.model.FlipperRpcInformation
import com.flipperdevices.bridge.rpcinfo.model.FlipperStorageInformation
import com.flipperdevices.bridge.rpcinfo.model.dataOrNull

private const val FLIPPER_RPC_INFO_PREFIX = "flipper_rpc_info"
private const val FLIPPER_RPC_INFO_INT_TOTAL = "${FLIPPER_RPC_INFO_PREFIX}_int_total"
private const val FLIPPER_RPC_INFO_INT_FREE = "${FLIPPER_RPC_INFO_PREFIX}_int_free"
private const val FLIPPER_RPC_INFO_EXT_TOTAL = "${FLIPPER_RPC_INFO_PREFIX}_ext_total"
private const val FLIPPER_RPC_INFO_EXT_FREE = "${FLIPPER_RPC_INFO_PREFIX}_ext_free"

object FlipperInformationMapping {
    fun convert(rpcInformation: FlipperRpcInformation): List<Pair<String, String>> {
        val tagsList = mutableListOf<Pair<String, String>>()
        rpcInformation.allFields.forEach { (key, value) ->
            tagsList.add("${FLIPPER_RPC_INFO_PREFIX}_$key" to value)
        }
        return tagsList
    }

    fun convert(storageInfo: FlipperStorageInformation): List<Pair<String, String>> {
        val tagsList = mutableListOf<Pair<String, String>>()
        storageInfo.externalStorageStatus.dataOrNull()?.let { stats ->
            if (stats is com.flipperdevices.bridge.rpcinfo.model.StorageStats.Loaded) {
                tagsList.add(FLIPPER_RPC_INFO_EXT_TOTAL to stats.total.toString())
                tagsList.add(FLIPPER_RPC_INFO_EXT_FREE to stats.free.toString())
            }
        }
        storageInfo.internalStorageStatus.dataOrNull()?.let { stats ->
            if (stats is com.flipperdevices.bridge.rpcinfo.model.StorageStats.Loaded) {
                tagsList.add(FLIPPER_RPC_INFO_INT_TOTAL to stats.total.toString())
                tagsList.add(FLIPPER_RPC_INFO_INT_FREE to stats.free.toString())
            }
        }
        return tagsList
    }
}
