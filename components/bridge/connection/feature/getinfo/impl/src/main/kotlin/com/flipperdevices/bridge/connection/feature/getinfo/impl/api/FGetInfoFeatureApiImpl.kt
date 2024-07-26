package com.flipperdevices.bridge.connection.feature.getinfo.impl.api

import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.impl.mapper.DeviceInfoKeyMapper
import com.flipperdevices.bridge.connection.feature.getinfo.impl.mapper.FGetInfoApiKeyMapper
import com.flipperdevices.bridge.connection.feature.getinfo.impl.utils.path
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiGroup
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiProperty
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequestPriority
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.property.getRequest
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
                main {
                    propertyGetRequest = getRequest { key = property.path }
                }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
            )
        )
        val forkValue = if (forkResponse.commandStatus == Flipper.CommandStatus.OK) {
            forkResponse.propertyGetResponse.value
        } else {
            error(forkResponse.commandStatus.toString())
        }

        return@runCatching forkValue
    }

    override fun get(group: FGetInfoApiGroup): Flow<Pair<FGetInfoApiProperty, String>> {
        return rpcFeatureApi.request(
            main {
                propertyGetRequest = getRequest { key = group.key }
            }.wrapToRequest()
        ).mapNotNull { it.propertyGetResponse }
            .map { mapper.map(group, it.key) to it.value }
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            rpcFeatureApi: FRpcFeatureApi
        ): FGetInfoFeatureApiImpl
    }
}