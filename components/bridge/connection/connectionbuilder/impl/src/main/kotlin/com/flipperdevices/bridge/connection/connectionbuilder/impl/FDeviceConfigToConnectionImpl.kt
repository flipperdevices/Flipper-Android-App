package com.flipperdevices.bridge.connection.connectionbuilder.impl

import com.flipperdevices.bridge.connection.common.api.DeviceConnectionApi
import com.flipperdevices.bridge.connection.common.api.FConnectedDeviceApi
import com.flipperdevices.bridge.connection.common.api.FDeviceConnectionConfig
import com.flipperdevices.bridge.connection.common.api.FTransportConnectionStatusListener
import com.flipperdevices.bridge.connection.common.api.di.ConnectionConfigQualifier
import com.flipperdevices.bridge.connection.connectionbuilder.api.FDeviceConfigToConnection
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FDeviceConfigToConnection::class)
class FDeviceConfigToConnectionImpl @Inject constructor(
    private val configToConnectionMap: Map<ConnectionConfigQualifier, DeviceConnectionApi<*, *>>
) : FDeviceConfigToConnection {
    override suspend fun <API : FConnectedDeviceApi, CONFIG : FDeviceConnectionConfig<API>> connect(
        scope: CoroutineScope,
        config: CONFIG,
        listener: FTransportConnectionStatusListener
    ): Result<API> = runCatching {
        val connectionApiUntyped = configToConnectionMap.entries.find { (qualifier, _) ->
            qualifier.configClazz.isInstance(
                config
            )
        } ?: throw NotImplementedError("Can't find connection for config $config")

        val connectionApi = connectionApiUntyped as? DeviceConnectionApi<API, CONFIG>
            ?: throw NotImplementedError("Can't map to connection api")

        connectionApi.connect(scope, config, listener).getOrThrow()
    }
}