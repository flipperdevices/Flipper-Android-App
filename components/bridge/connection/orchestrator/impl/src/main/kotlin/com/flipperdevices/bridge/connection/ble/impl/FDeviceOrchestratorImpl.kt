package com.flipperdevices.bridge.connection.ble.impl

import com.flipperdevices.bridge.connection.common.api.FDeviceConnectionConfig
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.sync.Mutex

class FDeviceOrchestratorImpl : FDeviceOrchestrator, LogTagProvider {
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
    }

    suspend fun disconnectCurrent() {

    }
}