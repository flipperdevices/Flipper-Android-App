package com.flipperdevices.bridge.connection.feature.restartrpc.impl

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialRestartApi
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FRestartRpcFeatureApi.Factory::class)
class FRestartRpcFeatureFactoryImpl @Inject constructor(
    private val restartRpcFactory: FRestartRpcFeatureApiImpl.InternalFactory
) : FRestartRpcFeatureApi.Factory {
    override fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FDeviceFeatureApi? {
        val serialApi = connectedDevice as? FSerialRestartApi ?: return null
        return restartRpcFactory(serialApi)
    }
}
