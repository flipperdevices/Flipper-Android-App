package com.flipperdevices.bridge.connection.feature.emulate.impl.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureQualifier
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.emulate.api.FEmulateFeatureApi
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.AppEmulateHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.EmulateHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.FlipperAppErrorHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.StartEmulateHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.StopEmulateHelper
import com.flipperdevices.bridge.connection.feature.emulate.impl.api.helpers.AppEmulateHelperImpl
import com.flipperdevices.bridge.connection.feature.emulate.impl.api.helpers.EmulateHelperImpl
import com.flipperdevices.bridge.connection.feature.emulate.impl.api.helpers.FlipperAppErrorHandlerImpl
import com.flipperdevices.bridge.connection.feature.emulate.impl.api.helpers.StartEmulateHelperImpl
import com.flipperdevices.bridge.connection.feature.emulate.impl.api.helpers.StopEmulateHelperImpl
import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@FDeviceFeatureQualifier(FDeviceFeature.EMULATE)
@ContributesMultibinding(AppGraph::class, FDeviceFeatureApi.Factory::class)
class FEmulateFeatureFactoryImpl @Inject constructor() : FDeviceFeatureApi.Factory {
    override suspend fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FDeviceFeatureApi? {
        val rpcApi = unsafeFeatureDeviceApi
            .getUnsafe(FRpcFeatureApi::class)
            ?: return null
        val fVersionFeatureApi = unsafeFeatureDeviceApi
            .getUnsafe(FVersionFeatureApi::class)
            ?: return null

        return object : FEmulateFeatureApi {
            private val flipperAppErrorHelper = FlipperAppErrorHandlerImpl(
                fRpcFeatureApi = rpcApi,
                fVersionFeatureApi = fVersionFeatureApi
            )
            private val stopEmulateHelper = StopEmulateHelperImpl(
                fRpcFeatureApi = rpcApi,
                fVersionFeatureApi = fVersionFeatureApi,
                scope = scope
            )
            private val appEmulateHelper = AppEmulateHelperImpl(
                fRpcFeatureApi = rpcApi
            )
            private val startEmulateHelper = StartEmulateHelperImpl(
                appEmulateHelper = appEmulateHelper,
                flipperAppErrorHelper = flipperAppErrorHelper,
                fRpcFeatureApi = rpcApi,
                fVersionFeatureApi = fVersionFeatureApi
            )
            private val emulateHelper = EmulateHelperImpl(
                startEmulateHelper = startEmulateHelper,
                stopEmulateHelper = stopEmulateHelper
            )

            override fun getAppEmulateHelper(): AppEmulateHelper {
                return appEmulateHelper
            }

            override fun getEmulateHelper(): EmulateHelper {
                return emulateHelper
            }

            override fun getFlipperErrorHelper(): FlipperAppErrorHelper {
                return flipperAppErrorHelper
            }

            override fun getStartEmulateHelper(): StartEmulateHelper {
                return startEmulateHelper
            }

            override fun getStopEmulateHelper(): StopEmulateHelper {
                return stopEmulateHelper
            }
        }
    }
}
