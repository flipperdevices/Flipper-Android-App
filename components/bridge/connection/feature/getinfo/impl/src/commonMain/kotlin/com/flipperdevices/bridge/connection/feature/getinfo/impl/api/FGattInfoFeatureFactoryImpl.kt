package com.flipperdevices.bridge.connection.feature.getinfo.impl.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureQualifier
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.bridge.connection.transport.common.api.meta.FTransportMetaInfoApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.CoroutineScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@FDeviceFeatureQualifier(FDeviceFeature.GATT_INFO)
@ContributesIntoMap(AppGraph::class, binding<FDeviceFeatureApi.Factory>())
class FGattInfoFeatureFactoryImpl @Inject constructor(
    private val factory: FGattInfoFeatureApiImpl.InternalFactory
) : FDeviceFeatureApi.Factory, LogTagProvider {
    override val TAG = "FGattInfoFeatureFactory"

    override suspend fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FDeviceFeatureApi? {
        val metaInfoApi = connectedDevice as? FTransportMetaInfoApi ?: return null

        return factory(
            metaInfoApi = metaInfoApi,
            scope = scope
        )
    }
}
