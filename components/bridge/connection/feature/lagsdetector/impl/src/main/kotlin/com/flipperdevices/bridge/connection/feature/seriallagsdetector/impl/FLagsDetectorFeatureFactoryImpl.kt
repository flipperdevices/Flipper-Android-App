package com.flipperdevices.bridge.connection.feature.seriallagsdetector.impl

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.seriallagsdetector.api.FLagsDetectorFeature
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope

@ContributesBinding(AppGraph::class, FLagsDetectorFeature.Factory::class)
class FLagsDetectorFeatureFactoryImpl constructor(
    private val lagsDetectorFeatureFactory: FLagsDetectorFeatureImpl.InternalFactory
) : FLagsDetectorFeature.Factory {
    override fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FDeviceFeatureApi? {
        val restartRpcFeature =
            unsafeFeatureDeviceApi.getUnsafe(FRestartRpcFeatureApi::class) ?: return null
        return lagsDetectorFeatureFactory(
            scope = scope,
            restartRpcFeatureApi = restartRpcFeature
        )
    }
}
