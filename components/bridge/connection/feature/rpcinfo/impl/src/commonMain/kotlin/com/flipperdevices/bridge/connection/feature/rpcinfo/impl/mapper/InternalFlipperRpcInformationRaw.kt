package com.flipperdevices.bridge.connection.feature.rpcinfo.impl.mapper

internal data class InternalFlipperRpcInformationRaw<KEY>(
    val otherFields: Map<KEY, String> = emptyMap()
)
