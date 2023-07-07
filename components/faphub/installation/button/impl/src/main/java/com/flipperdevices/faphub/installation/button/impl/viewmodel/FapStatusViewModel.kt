package com.flipperdevices.faphub.installation.button.impl.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.impl.helper.LoadFapHelper
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.flipperdevices.screenstreaming.api.ScreenStreamingFeatureEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tangle.viewmodel.VMInject

class FapStatusViewModel @VMInject constructor(
    private val stateManager: FapInstallationStateManager,
    private val queueApi: FapInstallationQueueApi,
    private val screenStreamingFeatureEntry: ScreenStreamingFeatureEntry,
    private val serviceProvider: FlipperServiceProvider,
    private val loadFapHelper: LoadFapHelper
) : LifecycleViewModel(), FlipperBleServiceConsumer {

    private val busyState = MutableStateFlow(false)
    fun getDialogState() = busyState.asStateFlow()

    init {
        serviceProvider.provideServiceApi(this, this)
    }

    fun getStateForApplicationId(
        fapButtonConfig: FapButtonConfig?
    ) = if (fapButtonConfig == null) {
        MutableStateFlow(FapState.NotInitialized)
    } else {
        stateManager.getFapStateFlow(
            applicationUid = fapButtonConfig.applicationUid,
            currentVersion = fapButtonConfig.version
        )
    }

    fun install(fapButtonConfig: FapButtonConfig?) {
        if (fapButtonConfig == null) {
            return
        }
        queueApi.enqueue(
            FapActionRequest.Install(
                applicationAlias = fapButtonConfig.applicationAlias,
                applicationUid = fapButtonConfig.applicationUid,
                toVersion = fapButtonConfig.version,
                categoryAlias = fapButtonConfig.categoryAlias,
                applicationName = fapButtonConfig.applicationName,
                iconUrl = fapButtonConfig.iconUrl
            )
        )
    }

    fun cancel(fapButtonConfig: FapButtonConfig?) {
        if (fapButtonConfig == null) {
            return
        }
        queueApi.enqueue(FapActionRequest.Cancel(fapButtonConfig.applicationUid))
    }

    fun update(
        fapButtonConfig: FapButtonConfig?,
        from: FapManifestItem
    ) {
        if (fapButtonConfig == null) {
            return
        }
        queueApi.enqueue(
            FapActionRequest.Update(
                from = from,
                toVersion = fapButtonConfig.version,
                iconUrl = fapButtonConfig.iconUrl,
                applicationName = fapButtonConfig.applicationName
            )
        )
    }

    fun openApp(config: FapButtonConfig?, navController: NavHostController) {
        if (config == null) return

        serviceProvider.provideServiceApi(this) { serviceApi ->
            viewModelScope.launch(Dispatchers.Default) {
                loadFapHelper.loadFap(
                    serviceApi = serviceApi,
                    scope = this,
                    config = config,
                    onSuccess = {
                        withContext(Dispatchers.Main) {
                            navController.navigate(screenStreamingFeatureEntry.ROUTE.name)
                        }
                    },
                    onBusy = { busyState.emit(true) }
                )
            }
        }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) = Unit
    fun closeDialog() {
        viewModelScope.launch {
            busyState.emit(false)
        }
    }
}
