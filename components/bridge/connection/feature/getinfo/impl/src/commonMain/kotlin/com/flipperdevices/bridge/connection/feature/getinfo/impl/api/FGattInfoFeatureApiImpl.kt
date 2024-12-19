package com.flipperdevices.bridge.connection.feature.getinfo.impl.api

import com.flipperdevices.bridge.connection.feature.getinfo.api.FGattInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGattInformation
import com.flipperdevices.bridge.connection.transport.common.api.meta.FTransportMetaInfoApi
import com.flipperdevices.bridge.connection.transport.common.api.meta.TransportMetaInfoKey
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlin.experimental.and

class FGattInfoFeatureApiImpl @AssistedInject constructor(
    @Assisted private val metaInfoApi: FTransportMetaInfoApi,
    @Assisted private val scope: CoroutineScope,
) : FGattInfoFeatureApi, LogTagProvider {
    override val TAG: String = "FGetInfoFeatureApi"

    private val gattInfo = MutableStateFlow(FGattInformation())

    override fun getGattInfoFlow() = gattInfo.asStateFlow()

    private fun collectGattInfo() {
        listOf(
            TransportMetaInfoKey.DEVICE_NAME,
            TransportMetaInfoKey.MANUFACTURER,
            TransportMetaInfoKey.BATTERY_LEVEL,
            TransportMetaInfoKey.BATTERY_POWER_STATE,
            TransportMetaInfoKey.SOFTWARE_VERSION,
            TransportMetaInfoKey.HARDWARE_VERSION
        ).forEach { key ->
            val flow = metaInfoApi.get(key)
                .onFailure { error(it) { "#collectGattInfo could not find flow for key $key" } }
                .getOrNull()
            (flow ?: emptyFlow())
                .filterNotNull()
                .onEach { byteArray ->
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

                            TransportMetaInfoKey.SOFTWARE_VERSION -> {
                                state.copy(
                                    softwareVersion = String(byteArray)
                                )
                            }

                            TransportMetaInfoKey.HARDWARE_VERSION -> {
                                state.copy(
                                    hardwareRevision = String(byteArray)
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
            metaInfoApi: FTransportMetaInfoApi,
            scope: CoroutineScope,
        ): FGattInfoFeatureApiImpl
    }

    companion object {
        private const val MAX_BATTERY_LEVEL = 100
        const val BATTERY_POWER_STATE_MASK: Byte = 0b0011_0000
    }
}
