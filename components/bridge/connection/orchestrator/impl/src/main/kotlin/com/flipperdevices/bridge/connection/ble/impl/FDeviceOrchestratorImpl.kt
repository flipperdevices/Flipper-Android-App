package com.flipperdevices.bridge.connection.ble.impl

import com.flipperdevices.bridge.connection.config.api.model.FDeviceBaseModel
import com.flipperdevices.bridge.connection.configbuilder.api.FDeviceConnectionConfigMapper
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, FDeviceOrchestrator::class)
class FDeviceOrchestratorImpl @Inject constructor(
    private val deviceHolderFactory: FDeviceHolderFactory,
    private val deviceConnectionConfigMapper: FDeviceConnectionConfigMapper
) : FDeviceOrchestrator, LogTagProvider {
    override val TAG = "FDeviceOrchestrator"

    private val transportListener = FTransportListenerImpl()
    private var currentDevice: FDeviceHolder<*>? = null
    private val mutex = Mutex()
    override suspend fun connect(config: FDeviceBaseModel) = withLock(mutex, "connect") {
        info { "Request connect for config $config" }

        disconnectInternalUnsafe()

        info { "Create new device" }
        currentDevice = deviceHolderFactory.build(
            config = deviceConnectionConfigMapper.getConnectionConfig(config),
            listener = { transportListener.onStatusUpdate(config, it) },
            onConnectError = {
                transportListener.onErrorDuringConnect(config, it)
                error(it) { "Failed connect" }
            },
            exceptionHandler = CoroutineExceptionHandler { _, exception ->
                transportListener.onErrorDuringConnect(config, exception)
            }
        )
    }

    override fun getState() = transportListener.getState()

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
