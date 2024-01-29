package com.flipperdevices.bridge.rpcinfo.impl.mapper

import com.flipperdevices.bridge.rpcinfo.model.FlipperRpcInformation

internal interface FlipperRpcInfoMapper {
    fun map(raw: InternalFlipperRpcInformationRaw): FlipperRpcInformation
}
