package com.flipperdevices.bridge.connection.feature.rpcinfo.impl.mapper

import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperRpcInformation

internal interface FlipperRpcInfoMapper<KEY> {
    fun map(raw: InternalFlipperRpcInformationRaw<KEY>): FlipperRpcInformation
}
