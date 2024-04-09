package com.flipperdevices.bridge.connection.ble.impl

import com.flipperdevices.bridge.connection.common.api.FDeviceConnectionConfig
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FDeviceOrchestrator::class)
class FDeviceOrchestratorImpl @Inject constructor(
    private val deviceHolderFactory: FDeviceHolder.Factory
) : FDeviceOrchestrator, LogTagProvider {
    override val TAG = "FDeviceOrchestrator"

    private val transportListener = FTransportListenerImpl()
    private var currentDevice: FDeviceHolder<*>? = null
    private val mutex = Mutex()
    override suspend fun connect(config: FDeviceConnectionConfig<*>) = withLock(mutex, "connect") {
        info { "Request connect for config $config" }

        disconnectCurrent()

        info { "Create new device" }
        currentDevice = deviceHolderFactory.build(
            config = config,
            listener = transportListener,
            onConnectError = {
                error(it) { "Failed connect" }
            }
        )
    }

    override suspend fun disconnectCurrent() = withLock(mutex, "disconnect") {
        disconnectInternalUnsafe()
    }

    private suspend fun disconnectInternalUnsafe() {
        val currentDeviceLocal = currentDevice
        if (currentDeviceLocal != null) {
            info { "Found current device, wait until disconnect" }
            currentDeviceLocal.disconnect()
        }
        currentDevice = null
    }


}