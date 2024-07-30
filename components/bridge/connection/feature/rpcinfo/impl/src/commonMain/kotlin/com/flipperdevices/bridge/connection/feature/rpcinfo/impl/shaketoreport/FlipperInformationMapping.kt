package com.flipperdevices.bridge.connection.feature.rpcinfo.impl.shaketoreport

import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperRpcInformation

private const val FLIPPER_RPC_INFO_PREFIX = "flipper_rpc_info"

object FlipperInformationMapping {
    fun convert(rpcInformation: FlipperRpcInformation): List<Pair<String, String>> {
        val tagsList = mutableListOf<Pair<String, String>>()
        rpcInformation.allFields.forEach { (key, value) ->
            tagsList.add("${FLIPPER_RPC_INFO_PREFIX}_$key" to value)
        }
        return tagsList
    }
}
