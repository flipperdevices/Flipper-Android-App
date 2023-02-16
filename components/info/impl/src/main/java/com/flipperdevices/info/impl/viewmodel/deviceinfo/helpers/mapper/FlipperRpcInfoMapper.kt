package com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers.mapper

import com.flipperdevices.info.api.model.FlipperRpcInformation

internal interface FlipperRpcInfoMapper {
    fun map(raw: InternalFlipperRpcInformationRaw): FlipperRpcInformation
}
