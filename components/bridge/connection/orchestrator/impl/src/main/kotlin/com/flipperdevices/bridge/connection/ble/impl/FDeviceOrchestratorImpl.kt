package com.flipperdevices.bridge.connection.ble.impl

import com.flipperdevices.bridge.connection.common.api.FDeviceConnectionConfig
import com.flipperdevices.bridge.connection.connectionbuilder.api.FDeviceConfigToConnection
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FDeviceOrchestrator::class)
class FDeviceOrchestratorImpl @Inject constructor(
    private val deviceConnectionHelper: FDeviceConfigToConnection
) : FDeviceOrchestrator, LogTagProvider {
    override val TAG = "FDeviceOrchestrator"

    private val transportListener = FTransportListenerImpl()
    private var currentDeviceScope: CoroutineScope? = null
    private val mutex = Mutex()
    override suspend fun connect(config: FDeviceConnectionConfig<*>) = withLock(mutex) {
        info { "Request connect for config $config" }

        info { "Wait until disconnect" }
        val deviceScope = currentDeviceScope
        if (deviceScope != null) {
            info { "Detect existing scope, start canceling" }
            deviceScope.cancel()
        }
        deviceConnectionHelper.connect(
            scope = CoroutineScope(Dispatchers.Default),
            config = config,
            listener = transportListener
        )
    }

}