package com.flipperdevices.bridge.connection.feature.rpcinfo.impl.mapper

import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperRpcInformation

internal interface FlipperRpcInfoMapper {
    fun map(raw: InternalFlipperRpcInformationRaw): FlipperRpcInformation
}
