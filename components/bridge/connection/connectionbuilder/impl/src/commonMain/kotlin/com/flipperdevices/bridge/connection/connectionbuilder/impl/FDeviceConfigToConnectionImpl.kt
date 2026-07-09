package com.flipperdevices.bridge.connection.connectionbuilder.impl

import com.flipperdevices.bridge.connection.connectionbuilder.api.FDeviceConfigToConnection
import com.flipperdevices.bridge.connection.transport.common.api.DeviceConnectionApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.bridge.connection.transport.common.api.FDeviceConnectionConfig
import com.flipperdevices.bridge.connection.transport.common.api.FTransportConnectionStatusListener
import com.flipperdevices.bridge.connection.transport.common.api.di.DeviceConnectionApiHolder
import com.flipperdevices.core.di.AppGraph
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import kotlin.reflect.KClass

@ContributesBinding(AppGraph::class, binding<FDeviceConfigToConnection>())
class FDeviceConfigToConnectionImpl @Inject constructor(
    private val configToConnectionMap: Map<KClass<*>, DeviceConnectionApiHolder>
) : FDeviceConfigToConnection {
    override suspend fun <API : FConnectedDeviceApi, CONFIG : FDeviceConnectionConfig<API>> connect(
        scope: CoroutineScope,
        config: CONFIG,
        listener: FTransportConnectionStatusListener
    ): Result<API> = runCatching {
        val connectionApiUntyped = configToConnectionMap.entries.find { (qualifier, _) ->
            qualifier.isInstance(
                config
            )
        } ?: throw NotImplementedError("Can't find connection for config $config")

        @Suppress("UNCHECKED_CAST")
        val connectionApi =
            connectionApiUntyped.value.deviceConnectionApi as? DeviceConnectionApi<API, CONFIG>
                ?: throw NotImplementedError("Can't map to connection api")

        connectionApi.connect(scope, config, listener).getOrThrow()
    }
}
