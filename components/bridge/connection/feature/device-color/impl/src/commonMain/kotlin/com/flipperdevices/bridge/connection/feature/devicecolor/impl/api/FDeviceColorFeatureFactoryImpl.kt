package com.flipperdevices.bridge.connection.feature.devicecolor.impl.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureQualifier
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.CoroutineScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@FDeviceFeatureQualifier(FDeviceFeature.DEVICE_COLOR)
@ContributesIntoMap(AppGraph::class, binding<FDeviceFeatureApi.Factory>())
class FDeviceColorFeatureFactoryImpl @Inject constructor(
    private val factory: FDeviceColorFeatureApiImpl.InternalFactory
) : FDeviceFeatureApi.Factory, LogTagProvider {
    override val TAG = "FDeviceColorFeatureFactory"

    override suspend fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FDeviceFeatureApi? {
        val fGetInfoFeatureApi = unsafeFeatureDeviceApi
            .getUnsafe(FGetInfoFeatureApi::class)
            ?: return null
        return factory(
            fGetInfoFeatureApi = fGetInfoFeatureApi,
        )
    }
}
