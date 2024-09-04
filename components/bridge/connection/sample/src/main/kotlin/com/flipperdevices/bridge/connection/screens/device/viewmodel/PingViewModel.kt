@file:OptIn(ExperimentalStdlibApi::class)

package com.flipperdevices.bridge.connection.screens.device.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.rpcinfo.api.FRpcInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.connection.feature.storageinfo.model.dataOrNull
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.system.PingRequest
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
class PingViewModel @Inject constructor(
    private val featureProvider: FFeatureProvider,
    orchestrator: FDeviceOrchestrator
) : DecomposeViewModel() {
    private val logLines = MutableStateFlow(persistentListOf("Init PingViewModel"))

    init {
        orchestrator.getState().onEach {
            log("Device connect status is: ${it::class.simpleName}")
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            featureProvider.get<FRpcFeatureApi>()
                .flatMapLatest { featureStatusSupport ->
                    log("Receive rpc api status support ${featureStatusSupport::class.simpleName}")
                    if (featureStatusSupport !is FFeatureStatus.Supported) {
                        log("Device api don't support rpc, so skip subscribe on bytes")
                        return@flatMapLatest flowOf()
                    }
                    log("Subscribe to receive bytes flow")
                    featureStatusSupport.featureApi.notificationFlow()
                }.collect {
                    log("Receive bytes: $it ")
                }
        }
        viewModelScope.launch {
            val alreadyPrintedKeys = mutableSetOf<String>()
            featureProvider.get<FRpcInfoFeatureApi>()
                .filterIsInstance<FFeatureStatus.Supported<FRpcInfoFeatureApi>>()
                .flatMapLatest {
                    it.featureApi.getRpcInformationFlow()
                }.onEach { rpcInfo ->
                    val data = rpcInfo.dataOrNull()
                    if (data == null) {
                        log("RpcInfo feature is $rpcInfo")
                    } else {
                        for ((key, value) in data.allFields.entries) {
                            if (alreadyPrintedKeys.contains(key).not()) {
                                alreadyPrintedKeys.add(key)
                                log("$key: $value")
                            }
                        }
                    }
                    if (rpcInfo is FlipperInformationStatus.Ready) {
                        log("Request complete")
                    }
                }.launchIn(viewModelScope)
        }
    }

    fun getLogLinesState() = logLines.asStateFlow()

    fun sendPing() = viewModelScope.launch {
        log("Request send ping")
        val requestApi = featureProvider.getSync<FRpcFeatureApi>()
        info { "Receive requestApi: $requestApi" }
        requestApi?.requestWithoutAnswer(
            Main(
                system_ping_request = PingRequest()
            ).wrapToRequest()
        )
        info { "Send ping request successful" }
    }

    fun invalidateRpcInfo() = viewModelScope.launch {
        val rpcInfoApi = featureProvider.get<FRpcInfoFeatureApi>()
            .filterIsInstance<FFeatureStatus.Supported<FRpcInfoFeatureApi>>()
            .first().featureApi
        rpcInfoApi.invalidate(viewModelScope, force = true)
    }

    private fun log(text: String) {
        logLines.update { it.add(text) }
    }
}
