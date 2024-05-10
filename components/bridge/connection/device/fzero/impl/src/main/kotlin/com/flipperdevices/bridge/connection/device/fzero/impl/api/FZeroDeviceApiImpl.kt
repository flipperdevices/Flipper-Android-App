package com.flipperdevices.bridge.connection.device.fzero.impl.api

import com.flipperdevices.bridge.connection.device.fzero.api.FZeroDeviceApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.core.di.AppGraph
import dagger.assisted.Assisted
import kotlinx.coroutines.CoroutineScope
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, FZeroDeviceApi.Factory::class)
class FZeroDeviceApiImpl(
    @Assisted private val scope: CoroutineScope,
    @Assisted private val connectedDevice: FConnectedDeviceApi,
    private val rpcFeatureFactory: FRpcFeatureApi.Factory
) : FZeroDeviceApi {
    override fun getFeatureApi(feature: FDeviceFeature): FDeviceFeatureApi? {
        return when (feature) {
            FDeviceFeature.RPC -> (connectedDevice as? FSerialDeviceApi)?.let { rpcFeatureFactory(scope, it) }
        }
    }
}
