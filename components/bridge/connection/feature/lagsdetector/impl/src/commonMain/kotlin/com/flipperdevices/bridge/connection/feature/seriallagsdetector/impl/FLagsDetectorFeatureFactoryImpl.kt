package com.flipperdevices.bridge.connection.feature.seriallagsdetector.impl

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureQualifier
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@FDeviceFeatureQualifier(FDeviceFeature.SERIAL_LAGS_DETECTOR)
@ContributesMultibinding(AppGraph::class, FDeviceFeatureApi.Factory::class)
class FLagsDetectorFeatureFactoryImpl @Inject constructor(
    private val lagsDetectorFeatureFactory: FLagsDetectorFeatureImpl.InternalFactory
) : FDeviceFeatureApi.Factory {
    override suspend fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FDeviceFeatureApi? {
        val restartRpcFeature =
            unsafeFeatureDeviceApi.getUnsafe(FRestartRpcFeatureApi::class) ?: return null
        val flipperActionNotifier = (connectedDevice as? FSerialDeviceApi)
            ?.getActionNotifier()
            ?: return null
        return lagsDetectorFeatureFactory(
            scope = scope,
            restartRpcFeatureApi = restartRpcFeature,
            flipperActionNotifier = flipperActionNotifier
        )
    }
}
