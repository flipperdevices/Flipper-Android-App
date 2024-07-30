package com.flipperdevices.bridge.connection.feature.rpc.impl

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureQualifier
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.getUnsafe
import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.seriallagsdetector.api.FLagsDetectorFeature
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@FDeviceFeatureQualifier(FDeviceFeature.RPC)
@ContributesMultibinding(AppGraph::class, FDeviceFeatureApi.Factory::class)
class FRpcFeatureFactoryImpl @Inject constructor(
    private val rpcFeatureFactory: FRpcFeatureApiImpl.InternalFactory
) : FDeviceFeatureApi.Factory {
    override suspend fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FDeviceFeatureApi? {
        val serialDeviceApi = connectedDevice as? FSerialDeviceApi ?: return null
        val lagsDetector =
            unsafeFeatureDeviceApi.getUnsafe(FLagsDetectorFeature::class) ?: return null
        val restartFeatureApi =
            unsafeFeatureDeviceApi.getUnsafe(FRestartRpcFeatureApi::class) ?: return null
        return rpcFeatureFactory(
            scope = scope,
            serialApi = serialDeviceApi,
            lagsDetector = lagsDetector,
            restartApiFeature = restartFeatureApi
        )
    }
}
