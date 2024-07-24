package com.flipperdevices.bridge.connection.feature.rpcinfo.impl.fullinforpc

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.rpcinfo.impl.mapper.NewFlipperRpcInfoMapper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.property.getRequest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

private val PROPERTY_KEYS = listOf("devinfo", "pwrinfo", "pwrdebug")

internal class NewFlipperFullInfoRpcApi : FlipperFullInfoRpcApi(
    NewFlipperRpcInfoMapper()
) {
    override suspend fun getRawDataFlow(
        requestApi: FRpcFeatureApi,
        onNewPair: suspend (key: String, value: String) -> Unit
    ) {
        PROPERTY_KEYS.map { propertyKey ->
            requestApi.request(
                main {
                    propertyGetRequest = getRequest { key = propertyKey }
                }.wrapToRequest()
            ).map { it to propertyKey }
        }.merge().collect { (response, propertyKey) ->
            if (!response.hasPropertyGetResponse()) {
                return@collect
            }

            onNewPair(
                "${propertyKey}_" + response.propertyGetResponse.key,
                response.propertyGetResponse.value
            )
        }
    }
}
