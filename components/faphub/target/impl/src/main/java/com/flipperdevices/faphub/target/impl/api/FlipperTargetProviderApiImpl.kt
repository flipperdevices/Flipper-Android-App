package com.flipperdevices.faphub.target.impl.api

import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FlipperTargetProviderApi::class)
class FlipperTargetProviderApiImpl @Inject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val fetcher: FlipperSdkFetcher
) : FlipperTargetProviderApi, LogTagProvider {
    override val TAG = "FlipperTargetProviderApi"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val targetFlow = MutableStateFlow<FlipperTarget>(FlipperTarget.Retrieving)

    init {
        scope.launch {
            subscribe()
        }
    }

    override fun getFlipperTarget() = targetFlow.asStateFlow()

    override suspend fun getFlipperTargetSync(): Result<FlipperTarget.Received> = runCatching {
        return@runCatching when (
            val receivedTarget = getFlipperTarget().first {
                it != FlipperTarget.Retrieving
            }
        ) {
            is FlipperTarget.Received -> receivedTarget
            FlipperTarget.Retrieving -> error("Please, wait until target is ready")
            FlipperTarget.Unsupported -> error("Fap catalog unsupported on this api version")
        }
    }

    private suspend fun subscribe() {
        info { "Start subscribe" }
        val serviceApi = serviceProvider.getServiceApi()
        serviceApi.flipperVersionApi.getVersionInformationFlow().collect { version ->
            info { "Receive version $version" }
            val sdkVersion = fetcher.getSdkApi(serviceApi.requestApi, version)
            info { "Sdk version is $sdkVersion" }
            val newTargetState = when (sdkVersion) {
                FlipperSdkVersion.Unsupported,
                FlipperSdkVersion.Error -> FlipperTarget.Unsupported

                FlipperSdkVersion.InProgress -> FlipperTarget.Retrieving
                is FlipperSdkVersion.Received -> FlipperTarget.Received(
                    target = "f7",
                    sdk = sdkVersion.sdk
                )
            }
            info { "New target state is $newTargetState" }
            targetFlow.emit(newTargetState)
        }
    }
}
