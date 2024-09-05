package com.flipperdevices.bridge.connection.feature.storage.impl

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureQualifier
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.getUnsafe
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.impl.fm.FFileStorageMD5ApiImpl
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject


@FDeviceFeatureQualifier(FDeviceFeature.STORAGE)
@ContributesMultibinding(AppGraph::class, FDeviceFeatureApi.Factory::class)
class FFileStorageApiFactoryImpl @Inject constructor() : FDeviceFeatureApi.Factory {
    override suspend fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FDeviceFeatureApi? {
        val rpcApi = unsafeFeatureDeviceApi.getUnsafe<FRpcFeatureApi>() ?: return null
        return FFileStorageApiImpl(
            md5Api = FFileStorageMD5ApiImpl(rpcApi)
        )
    }
}