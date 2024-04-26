@file:OptIn(ExperimentalStdlibApi::class)

package com.flipperdevices.bridge.connection.screens.device.viewmodel

import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.bridge.protobuf.toDelimitedBytes
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.system.pingRequest
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class PingViewModel @Inject constructor(
    private val orchestrator: FDeviceOrchestrator
) : DecomposeViewModel() {
    private val logLines = MutableStateFlow(persistentListOf("Init PingViewModel"))

    init {
        orchestrator.getState()
            .filterIsInstance<FDeviceConnectStatus.Connected>()
            .flatMapLatest { device ->
                log("Receive connected status, subscribe on bytes")
                val serialDeviceApi = device.deviceApi as? FSerialDeviceApi
                if (serialDeviceApi == null) {
                    log("Device api don't support serial, so skip subscribe on bytes")
                    return@flatMapLatest flowOf(byteArrayOf())
                }
                serialDeviceApi.getReceiveBytesFlow()
            }.onEach {
                log("Receive bytes: ${it.toHexString()} ")
            }.launchIn(viewModelScope)
    }

    fun getLogLinesState() = logLines.asStateFlow()

    fun sendPing() = viewModelScope.launch {
        log("Request send ping")
        val device = orchestrator.getState().first() as? FDeviceConnectStatus.Connected
        if (device == null) {
            log("Current state not connected, so skip ping request")
            return@launch
        }
        val serialDeviceApi = device.deviceApi as? FSerialDeviceApi

        if (serialDeviceApi == null) {
            log("Device api don't support serial, so skip ping request")
            return@launch
        }
        val bytes = main {
            systemPingRequest = pingRequest { }
        }.toDelimitedBytes()
        log("Send ping request ${bytes.size} bytes (${bytes.toHexString()})")
        serialDeviceApi.sendBytes(bytes)
    }

    private fun log(text: String) {
        logLines.update { it.add(text) }
    }
}
