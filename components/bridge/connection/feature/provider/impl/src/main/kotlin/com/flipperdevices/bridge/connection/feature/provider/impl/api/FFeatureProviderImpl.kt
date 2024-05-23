package com.flipperdevices.bridge.connection.feature.provider.impl.api

import com.flipperdevices.bridge.connection.device.common.api.FDeviceApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.impl.utils.FDeviceConnectStatusToDeviceApi
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
@ContributesBinding(AppGraph::class, FFeatureProvider::class)
class FFeatureProviderImpl @Inject constructor(
    private val orchestrator: FDeviceOrchestrator,
    private val deviceApiMapper: FDeviceConnectStatusToDeviceApi
) : FFeatureProvider {
    private val scope = CoroutineScope(FlipperDispatchers.workStealingDispatcher)

    private val deviceStateFlow = MutableStateFlow<FDeviceApi?>(null)

    init {
        orchestrator.getState().map { status ->
            when (status) {
                is FDeviceConnectStatus.Connected -> deviceApiMapper.get(status)
                is FDeviceConnectStatus.Connecting,
                is FDeviceConnectStatus.Disconnecting,
                is FDeviceConnectStatus.Disconnected -> null
            }
        }.onEach {
            deviceStateFlow.emit(it)
        }.launchIn(scope)
    }

    override fun <T : FDeviceFeatureApi> get(clazz: KClass<T>): Flow<FFeatureStatus<T>> {
        return deviceStateFlow.map { deviceApi ->
            return@map if (deviceApi == null) {
                FFeatureStatus.Retrieving
            } else {
                val feature = deviceApi.get(clazz)
                    ?: return@map FFeatureStatus.Unsupported
                return@map FFeatureStatus.Supported<T>(feature)
            }
        }
    }

    override suspend fun <T : FDeviceFeatureApi> getSync(clazz: KClass<T>): T? {
        return get(clazz)
            .filter { it !is FFeatureStatus.Retrieving }
            .map { featureStatus ->
                when (featureStatus) {
                    is FFeatureStatus.Supported -> featureStatus.featureApi
                    FFeatureStatus.NotFound,
                    FFeatureStatus.Unsupported -> null

                    FFeatureStatus.Retrieving -> error("Impossible situation")
                }
            }.first()
    }
}
