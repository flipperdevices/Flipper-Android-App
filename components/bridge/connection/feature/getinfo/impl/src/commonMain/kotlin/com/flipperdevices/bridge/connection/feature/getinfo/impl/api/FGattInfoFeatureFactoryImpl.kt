package com.flipperdevices.bridge.connection.feature.getinfo.impl.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureQualifier
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.bridge.connection.transport.common.api.meta.FTransportMetaInfoApi
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

// todo move to shared const
private val API_SUPPORTED_GET_REQUEST = SemVer(
    majorVersion = 0,
    minorVersion = 14
)

@FDeviceFeatureQualifier(FDeviceFeature.GATT_INFO)
@ContributesMultibinding(AppGraph::class, FDeviceFeatureApi.Factory::class)
class FGattInfoFeatureFactoryImpl @Inject constructor(
    private val factory: FGattInfoFeatureApiImpl.InternalFactory
) : FDeviceFeatureApi.Factory, LogTagProvider {
    override val TAG = "FGattInfoFeatureFactory"

    override suspend fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FDeviceFeatureApi? {
        val versionApi = unsafeFeatureDeviceApi.getUnsafe(FVersionFeatureApi::class) ?: return null
        info { "Start request supported state for api level $API_SUPPORTED_GET_REQUEST" }
        val isSupported = versionApi.isSupported(API_SUPPORTED_GET_REQUEST)
        if (!isSupported) {
            error { "Failed init FGattInfoFeatureApi, because isSupported=false" }
            return null
        }

        val metaInfoApi = connectedDevice as? FTransportMetaInfoApi ?: return null

        return factory(
            metaInfoApi = metaInfoApi,
            scope = scope
        )
    }
}
