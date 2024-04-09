package com.flipperdevices.bridge.connection.orchestrator.api

import com.flipperdevices.bridge.connection.common.api.FDeviceConnectionConfig

interface FDeviceOrchestrator {
    suspend fun connect(config: FDeviceConnectionConfig<*>)

    suspend fun disconnectCurrent()
}