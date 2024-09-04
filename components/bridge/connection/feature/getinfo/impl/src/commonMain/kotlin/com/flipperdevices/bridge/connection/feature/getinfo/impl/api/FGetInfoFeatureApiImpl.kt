package com.flipperdevices.bridge.connection.feature.getinfo.impl.api

import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.impl.mapper.FGetInfoApiKeyMapper
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiGroup
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiProperty
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.protobuf.CommandStatus
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.property.GetRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class FGetInfoFeatureApiImpl @AssistedInject constructor(
    @Assisted private val rpcFeatureApi: FRpcFeatureApi
) : FGetInfoFeatureApi {
    private val mapper = FGetInfoApiKeyMapper()

    override suspend fun get(property: FGetInfoApiProperty) = runCatching {
        val forkResponse = rpcFeatureApi.request(
            flowOf(
                Main(
                    property_get_request = GetRequest(key = property.path)
                ).wrapToRequest()
            )
        )
        val forkValue = if (forkResponse.command_status == CommandStatus.OK) {
            forkResponse.property_get_response?.value_
        } else {
            null
        }

        if (forkValue == null) {
            error(forkResponse.command_status.toString())
        }

        return@runCatching forkValue
    }

    override fun get(group: FGetInfoApiGroup): Flow<Pair<FGetInfoApiProperty, String>> {
        return rpcFeatureApi.request(
            Main(
                property_get_request = GetRequest(key = group.key)
            ).wrapToRequest()
        ).mapNotNull { it.property_get_response }
            .map { mapper.map(group, it.key) to it.value_ }
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            rpcFeatureApi: FRpcFeatureApi
        ): FGetInfoFeatureApiImpl
    }
}
