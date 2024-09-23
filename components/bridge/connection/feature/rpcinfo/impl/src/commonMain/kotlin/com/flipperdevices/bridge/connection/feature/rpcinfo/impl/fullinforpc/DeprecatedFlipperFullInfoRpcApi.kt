package com.flipperdevices.bridge.connection.feature.rpcinfo.impl.fullinforpc

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.rpcinfo.impl.mapper.DeprecatedFlipperRpcInfoMapper
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.system.DeviceInfoRequest
import com.flipperdevices.protobuf.system.PowerInfoRequest

internal class DeprecatedFlipperFullInfoRpcApi : FlipperFullInfoRpcApi<String>(
    DeprecatedFlipperRpcInfoMapper()
) {
    override suspend fun getRawDataFlow(
        requestApi: FRpcFeatureApi,
        onNewPair: suspend (key: String, value: String) -> Unit
    ) {
        requestApi.request(
            Main(
                system_device_info_request = DeviceInfoRequest()
            ).wrapToRequest()
        ).collect { response ->
            val systemDeviceInfoResponse = response.getOrNull()?.system_device_info_response ?: return@collect
            onNewPair(
                systemDeviceInfoResponse.key,
                systemDeviceInfoResponse.value_
            )
        }
        requestApi.request(
            Main(
                system_power_info_request = PowerInfoRequest()
            ).wrapToRequest()
        ).collect { response ->
            val systemPowerInfoResponse = response.getOrNull()?.system_power_info_response ?: return@collect
            onNewPair(
                systemPowerInfoResponse.key,
                systemPowerInfoResponse.value_
            )
        }
    }
}
