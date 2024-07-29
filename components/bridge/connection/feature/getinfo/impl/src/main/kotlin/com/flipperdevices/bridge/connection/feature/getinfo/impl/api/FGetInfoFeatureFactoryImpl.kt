package com.flipperdevices.bridge.connection.feature.getinfo.impl.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureQualifier
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

private val API_SUPPORTED_GET_REQUEST = SemVer(
    majorVersion = 0,
    minorVersion = 14
)

@FDeviceFeatureQualifier(FDeviceFeature.GET_INFO)
@ContributesMultibinding(AppGraph::class, FDeviceFeatureApi.Factory::class)
class FGetInfoFeatureFactoryImpl @Inject constructor(
    private val factory: FGetInfoFeatureApiImpl.InternalFactory
) : FDeviceFeatureApi.Factory, LogTagProvider {
    override val TAG = "FGetInfoFeatureFactory"

    override suspend fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FDeviceFeatureApi? {
        val versionApi = unsafeFeatureDeviceApi.getUnsafe(FVersionFeatureApi::class) ?: return null
        info { "Start request supported state for api level $API_SUPPORTED_GET_REQUEST" }
        val isSupported = versionApi.isSupported(API_SUPPORTED_GET_REQUEST)
        if (!isSupported) {
            error { "Failed init FGetInfoFeatureApi, because isSupported=false" }
            return null
        }
        info { "Version $API_SUPPORTED_GET_REQUEST supported, so continue building FGetInfoFeatureApi" }

        val rpcApi = unsafeFeatureDeviceApi.getUnsafe(FRpcFeatureApi::class) ?: return null

        return factory(
            rpcApi
        )
    }
}
