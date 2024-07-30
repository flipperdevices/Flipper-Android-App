package com.flipperdevices.bridge.connection.feature.rpcstats.impl.api

import com.flipperdevices.bridge.connection.feature.common.api.FOnDeviceReadyFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.getUnsafe
import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.storageinfo.api.FStorageInfoFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@ContributesMultibinding(AppGraph::class, FOnDeviceReadyFeatureApi.Factory::class)
class FRpcStatsOnDeviceReadyFeatureFactoryImpl @Inject constructor(
    private val factory: FRpcStatsOnDeviceReadyFeatureApiImpl.InternalFactory
) : FOnDeviceReadyFeatureApi.Factory {
    override suspend fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FOnDeviceReadyFeatureApi? {
        val storageApi =
            unsafeFeatureDeviceApi.getUnsafe(FStorageInfoFeatureApi::class) ?: return null
        val getInfo = unsafeFeatureDeviceApi.getUnsafe(FGetInfoFeatureApi::class)

        return factory(
            scope = scope,
            storageFeatureApi = storageApi,
            getInfoFeatureApiNullable = getInfo
        )
    }
}
