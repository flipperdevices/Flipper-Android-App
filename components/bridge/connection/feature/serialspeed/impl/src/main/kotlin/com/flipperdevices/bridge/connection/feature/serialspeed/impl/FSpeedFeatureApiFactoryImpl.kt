package com.flipperdevices.bridge.connection.feature.serialspeed.impl

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.serialspeed.api.FSpeedFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FSpeedFeatureApi.Factory::class)
class FSpeedFeatureApiFactoryImpl @Inject constructor(
    private val speedFeatureInternalFactory: FSpeedFeatureApiImpl.InternalFactory
) : FSpeedFeatureApi.Factory {
    override fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FDeviceFeatureApi? {
        val serialApi = connectedDevice as? FSerialDeviceApi ?: return null
        return speedFeatureInternalFactory(serialApi)
    }
}
