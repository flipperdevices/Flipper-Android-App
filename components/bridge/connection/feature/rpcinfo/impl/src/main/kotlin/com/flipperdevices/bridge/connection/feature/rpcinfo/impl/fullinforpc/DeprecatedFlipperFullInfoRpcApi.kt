package com.flipperdevices.bridge.connection.feature.rpcinfo.impl.fullinforpc

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.rpcinfo.impl.mapper.DeprecatedFlipperRpcInfoMapper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.system.deviceInfoRequest
import com.flipperdevices.protobuf.system.powerInfoRequest

internal class DeprecatedFlipperFullInfoRpcApi : FlipperFullInfoRpcApi<String>(
    DeprecatedFlipperRpcInfoMapper()
) {
    override suspend fun getRawDataFlow(
        requestApi: FRpcFeatureApi,
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
