package com.flipperdevices.faphub.target.impl.api

import com.flipperdevices.bridge.connection.feature.protocolversion.api.FSdkVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import com.flipperdevices.faphub.target.model.FlipperTarget
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, FlipperTargetProviderApi::class)
class FlipperTargetProviderApiImpl @Inject constructor(
    private val fFeatureProviderApi: FFeatureProvider,
    private val fDeviceOrchestrator: FDeviceOrchestrator
) : FlipperTargetProviderApi, LogTagProvider {
    override val TAG = "FlipperTargetProviderApi"

    private val scope = CoroutineScope(SupervisorJob() + FlipperDispatchers.workStealingDispatcher)
    private val targetFlow = MutableStateFlow<FlipperTarget?>(null)

    init {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            subscribe()
        }
    }

    override fun getFlipperTarget() = targetFlow.asStateFlow()

    private suspend fun subscribe() {
        info { "Start subscribe" }

        combine(
            fDeviceOrchestrator.getState(),
            fFeatureProviderApi.get<FSdkVersionFeatureApi>()
        ) { connectionState, status ->
            when (connectionState) {
                is FDeviceConnectStatus.Disconnecting,
                is FDeviceConnectStatus.Disconnected,
                is FDeviceConnectStatus.Connecting -> {
                    targetFlow.emit(FlipperTarget.NotConnected)
                    return@combine
                }

                is FDeviceConnectStatus.Connected -> Unit
            }

            targetFlow.emit(null)
            val semVer = when (status) {
                is FFeatureStatus.Supported -> status.featureApi.getSdkVersion().getOrNull()

                FFeatureStatus.Retrieving -> {
                    targetFlow.emit(FlipperTarget.NotConnected)
                    return@combine
                }

                FFeatureStatus.NotFound,
                FFeatureStatus.Unsupported -> {
                    targetFlow.emit(FlipperTarget.Unsupported)
                    return@combine
                }
            }
            info { "Sdk version is $semVer" }

            val targetState = when (semVer) {
                null -> null
                else -> FlipperTarget.Received(
                    target = "f7",
                    sdk = semVer
                )
            }
            targetFlow.emit(targetState)
        }.collect()
    }
}
