package com.flipperdevices.bridge.rpcinfo.impl.fullinforpc

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.rpcinfo.impl.mapper.DeprecatedFlipperRpcInfoMapper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.system.deviceInfoRequest
import com.flipperdevices.protobuf.system.powerInfoRequest

internal class DeprecatedFlipperFullInfoRpcApi : FlipperFullInfoRpcApi(
    DeprecatedFlipperRpcInfoMapper()
) {
    override suspend fun getRawDataFlow(
        requestApi: FlipperRequestApi,
        onNewPair: suspend (key: String, value: String) -> Unit
    ) {
        requestApi.request(
            main {
                systemDeviceInfoRequest = deviceInfoRequest { }
            }.wrapToRequest()
        ).collect { response ->
            if (!response.hasSystemDeviceInfoResponse()) {
                return@collect
            }
            onNewPair(
                response.systemDeviceInfoResponse.key,
                response.systemDeviceInfoResponse.value
            )
        }
        requestApi.request(
            main {
                systemPowerInfoRequest = powerInfoRequest { }
            }.wrapToRequest()
        ).collect { response ->
            if (!response.hasSystemPowerInfoResponse()) {
                return@collect
            }
            onNewPair(
                response.systemPowerInfoResponse.key,
                response.systemPowerInfoResponse.value
            )
        }
    }
}
