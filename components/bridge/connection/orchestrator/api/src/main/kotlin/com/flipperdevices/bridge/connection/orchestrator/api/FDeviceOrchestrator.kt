package com.flipperdevices.bridge.connection.orchestrator.api

import com.flipperdevices.bridge.connection.transport.common.api.FDeviceConnectionConfig
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import kotlinx.coroutines.flow.StateFlow

interface FDeviceOrchestrator {

    fun getState(): StateFlow<FDeviceConnectStatus>

    suspend fun connect(config: FDeviceConnectionConfig<*>)

    suspend fun disconnectCurrent()
}
