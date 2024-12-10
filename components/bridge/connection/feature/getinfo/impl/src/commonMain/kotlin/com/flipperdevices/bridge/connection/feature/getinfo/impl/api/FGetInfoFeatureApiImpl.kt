package com.flipperdevices.bridge.connection.feature.getinfo.impl.api

import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.impl.mapper.FGetInfoApiKeyMapper
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGattInformation
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiGroup
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiProperty
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.transport.common.api.meta.FTransportMetaInfoApi
import com.flipperdevices.bridge.connection.transport.common.api.meta.TransportMetaInfoKey
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.property.GetRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlin.experimental.and

class FGetInfoFeatureApiImpl @AssistedInject constructor(
    @Assisted private val rpcFeatureApi: FRpcFeatureApi,
    @Assisted private val metaInfoApi: FTransportMetaInfoApi,
    @Assisted private val scope: CoroutineScope,
) : FGetInfoFeatureApi, LogTagProvider {
    override val TAG: String = "FGetInfoFeatureApi"
    private val mapper = FGetInfoApiKeyMapper()

    override suspend fun get(
        property: FGetInfoApiProperty
    ) = rpcFeatureApi.requestOnce(
        Main(
            property_get_request = GetRequest(key = property.path)
        ).wrapToRequest()
    ).mapCatching { it.property_get_response!!.value_ }

    override fun get(group: FGetInfoApiGroup): Flow<Pair<FGetInfoApiProperty, String>> {
        return rpcFeatureApi.request(
            Main(
                property_get_request = GetRequest(key = group.key)
            ).wrapToRequest()
        ).mapNotNull { it.getOrNull() }
            .mapNotNull { it.property_get_response }
            .map { mapper.map(group, it.key) to it.value_ }
    }

    private val gattInfo = MutableStateFlow(FGattInformation())

    override fun getGattInfoFlow(): MutableStateFlow<FGattInformation> = gattInfo

    private fun collectGattInfo() {
        listOf(
            TransportMetaInfoKey.DEVICE_NAME,
            TransportMetaInfoKey.MANUFACTURER,
            TransportMetaInfoKey.BATTERY_LEVEL,
            TransportMetaInfoKey.BATTERY_POWER_STATE
        ).forEach { key ->
            val flow = metaInfoApi.get(key)
                .onFailure { error(it) { "#collectGattInfo could not find flow for key $key" } }
                .getOrNull()
            (flow ?: emptyFlow())
                .filterNotNull()
                .onEach { byteArray ->
                    info { "#collectGattInfo array for $key ${byteArray.toList()}" }
                    gattInfo.update { state ->
                        when (key) {
                            TransportMetaInfoKey.DEVICE_NAME -> {
                                state.copy(deviceName = String(byteArray))
                            }

                            TransportMetaInfoKey.MANUFACTURER -> {
                                state.copy(manufacturerName = String(byteArray))
                            }

                            TransportMetaInfoKey.BATTERY_LEVEL -> {
                                state.copy(
                                    batteryLevel = byteArray.firstOrNull()
                                        ?.toFloat()
                                        ?.div(MAX_BATTERY_LEVEL)
                                        ?.coerceIn(0f, 1f)
                                )
                            }

                            TransportMetaInfoKey.BATTERY_POWER_STATE -> {
                                state.copy(
                                    isCharging = byteArray.firstOrNull()
                                        ?.and(BATTERY_POWER_STATE_MASK)
                                        ?.equals(BATTERY_POWER_STATE_MASK)
                                        ?: false
                                )
                            }

                            else -> state
                        }

                    }
                }.launchIn(scope)
        }
    }

    init {
        collectGattInfo()
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            rpcFeatureApi: FRpcFeatureApi,
            metaInfoApi: FTransportMetaInfoApi,
            scope: CoroutineScope,
        ): FGetInfoFeatureApiImpl
    }

    companion object {
        private const val MAX_BATTERY_LEVEL = 100
        const val BATTERY_POWER_STATE_MASK: Byte = 0b0011_0000
    }
}
