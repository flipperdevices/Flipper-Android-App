package com.flipperdevices.bridge.connection.orchestrator.api

import com.flipperdevices.bridge.connection.common.api.FDeviceConnectionConfig
import com.flipperdevices.bridge.connection.common.api.FInternalTransportConnectionStatus
import kotlinx.coroutines.flow.StateFlow

interface FDeviceOrchestrator {

    fun getState(): StateFlow<FInternalTransportConnectionStatus>
    suspend fun connect(config: FDeviceConnectionConfig<*>)

    suspend fun disconnectCurrent()
}
