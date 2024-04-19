package com.flipperdevices.faphub.target.impl.api

import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import com.flipperdevices.faphub.target.impl.model.FlipperSdkVersion
import com.flipperdevices.faphub.target.impl.utils.FlipperSdkFetcher
import com.flipperdevices.faphub.target.model.FlipperTarget
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val serviceProvider: FlipperServiceProvider,
    private val fetcher: FlipperSdkFetcher
) : FlipperTargetProviderApi, LogTagProvider {
    override val TAG = "FlipperTargetProviderApi"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val targetFlow = MutableStateFlow<FlipperTarget?>(null)

    init {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            subscribe()
        }
    }

    override fun getFlipperTarget() = targetFlow.asStateFlow()

    private suspend fun subscribe() {
        info { "Start subscribe" }
        val serviceApi = serviceProvider.getServiceApi()
        combine(
            serviceApi.connectionInformationApi.getConnectionStateFlow(),
            serviceApi.flipperVersionApi.getVersionInformationFlow()
        ) { connectionState, version ->
            if (!connectionState.isConnected) {
                when (connectionState) {
                    is ConnectionState.Disconnected -> targetFlow.emit(FlipperTarget.NotConnected)
                    else -> targetFlow.emit(null)
                }
                return@combine
            }
            targetFlow.emit(null)
            info { "Receive version $version" }
            val sdkVersion = fetcher.getSdkApi(serviceApi.requestApi, version)
            info { "Sdk version is $sdkVersion" }
            val newTargetState = when (sdkVersion) {
                FlipperSdkVersion.Unsupported,
                FlipperSdkVersion.Error -> FlipperTarget.Unsupported

                FlipperSdkVersion.InProgress -> null
                is FlipperSdkVersion.Received -> FlipperTarget.Received(
                    target = "f7",
                    sdk = sdkVersion.sdk
                )
            }
            info { "New target state is $newTargetState" }
            targetFlow.emit(newTargetState)
        }.collect()
    }
}
