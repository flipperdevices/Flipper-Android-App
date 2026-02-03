package com.flipperdevices.bridge.connection.feature.networkproxy.impl.api

import com.flipperdevices.bridge.connection.feature.common.api.FOnDeviceReadyFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.getUnsafe
import com.flipperdevices.bridge.connection.feature.networkproxy.api.FNetworkProxyFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@ContributesMultibinding(AppGraph::class, FOnDeviceReadyFeatureApi.Factory::class)
class FNetworkProxyOnDeviceReadyFactoryImpl @Inject constructor() : FOnDeviceReadyFeatureApi.Factory, LogTagProvider {
    override val TAG = "FNetworkProxyOnDeviceReadyFactory"

    override suspend fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FOnDeviceReadyFeatureApi? {
        // Request the network proxy feature to ensure it's initialized
        // This triggers its init block which sets up the notification listener
        val networkProxy = unsafeFeatureDeviceApi.getUnsafe(FNetworkProxyFeatureApi::class)
        info { "Network proxy feature initialized: ${networkProxy != null}" }

        // Return null since we don't need to provide an actual FOnDeviceReadyFeatureApi
        // We just needed to trigger initialization of the network proxy feature
        return null
    }
}
