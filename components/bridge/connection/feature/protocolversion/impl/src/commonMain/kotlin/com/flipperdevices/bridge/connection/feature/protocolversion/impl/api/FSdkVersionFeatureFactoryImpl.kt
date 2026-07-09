package com.flipperdevices.bridge.connection.feature.protocolversion.impl.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureQualifier
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.core.di.AppGraph
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.CoroutineScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@FDeviceFeatureQualifier(FDeviceFeature.SDK_VERSION)
@ContributesIntoMap(AppGraph::class, binding<FDeviceFeatureApi.Factory>())
class FSdkVersionFeatureFactoryImpl @Inject constructor(
    private val factory: FSdkVersionFeatureApiImpl.InternalFactory
) : FDeviceFeatureApi.Factory {
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
