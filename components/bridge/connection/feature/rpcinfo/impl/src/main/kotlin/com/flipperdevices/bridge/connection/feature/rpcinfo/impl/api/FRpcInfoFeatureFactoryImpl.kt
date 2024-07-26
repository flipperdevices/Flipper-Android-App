package com.flipperdevices.bridge.connection.feature.rpcinfo.impl.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureQualifier
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpcinfo.api.FRpcInfoFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@FDeviceFeatureQualifier(FDeviceFeature.RPC_INFO)
@ContributesMultibinding(AppGraph::class, FDeviceFeatureApi.Factory::class)
class FRpcInfoFeatureFactoryImpl @Inject constructor(
    private val factory: FRpcInfoFeatureApiImpl.InternalFactory
) : FRpcInfoFeatureApi.Factory {
    override fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FDeviceFeatureApi? {
        val rpcFeatureApi = unsafeFeatureDeviceApi.getUnsafe(FRpcFeatureApi::class)
            ?: return null
        val versionApi = unsafeFeatureDeviceApi.getUnsafe(FVersionFeatureApi::class)
            ?: return null

        return factory(
            rpcFeatureApi = rpcFeatureApi,
            versionApi = versionApi
        )
    }
}