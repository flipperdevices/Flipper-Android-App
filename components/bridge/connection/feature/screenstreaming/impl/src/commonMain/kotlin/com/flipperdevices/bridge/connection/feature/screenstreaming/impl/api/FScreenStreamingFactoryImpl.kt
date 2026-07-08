package com.flipperdevices.bridge.connection.feature.screenstreaming.impl.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureQualifier
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.core.di.AppGraph
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.CoroutineScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@FDeviceFeatureQualifier(FDeviceFeature.SCREEN_STREAMING)
@ContributesIntoMap(AppGraph::class, binding<FDeviceFeatureApi.Factory>())
class FScreenStreamingFactoryImpl @Inject constructor(
    private val factory: FScreenStreamingFeatureApiImpl.InternalFactory
) : FDeviceFeatureApi.Factory {
    override suspend fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FDeviceFeatureApi? {
        val rpcApi = unsafeFeatureDeviceApi.getUnsafe(FRpcFeatureApi::class) ?: return null
        return factory(
            rpcFeatureApi = rpcApi,
        )
    }
}
