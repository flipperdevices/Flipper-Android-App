package com.flipperdevices.faphub.installation.button.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.impl.helper.LoadFapHelper
import com.flipperdevices.faphub.installation.button.impl.model.OpenFapState
import com.flipperdevices.screenstreaming.api.ScreenStreamingFeatureEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tangle.viewmodel.VMInject

class OpenFapViewModel @VMInject constructor(
    private val loadFapHelper: LoadFapHelper,
    private val screenStreamingFeatureEntry: ScreenStreamingFeatureEntry,
    private val serviceProvider: FlipperServiceProvider,
) : ViewModel(), LogTagProvider {
    override val TAG: String = "OpenFapViewModel"

    private val busyDialogState = MutableStateFlow(false)
    fun getDialogState() = busyDialogState.asStateFlow()

    private val openFapState = MutableStateFlow<OpenFapState>(OpenFapState.NotSupported)
    fun getOpenFapState() = openFapState.asStateFlow()

    init {
        processSupportRPC()
    }

    private fun processSupportRPC() {
        viewModelScope.launch(Dispatchers.Default) {
            val serviceApi = serviceProvider.getServiceApi()
            val version = serviceApi.flipperVersionApi.getVersionInformationFlow().first()

            if (version != null && version >= Constants.API_SUPPORTED_LOAD_FAP) {
                openFapState.emit(OpenFapState.Ready)
            }
        }
    }

    fun open(config: FapButtonConfig?, navController: NavHostController) {
        if (config == null) {
            info { "Cannot open because config in null" }
            return
        }

        if (openFapState.value !is OpenFapState.Ready) {
            info { "Cannot open because state not in ready" }
            return
        }

        viewModelScope.launch {
            openFapState.emit(OpenFapState.InProgress(config.applicationUid))

            withContext(Dispatchers.Default) {
                val serviceApi = serviceProvider.getServiceApi()

                loadFapHelper.loadFap(
                    serviceApi = serviceApi,
                    config = config,
                    onSuccess = { navigateToScreenStreaming(navController) },
                    onBusy = ::processBusyFlipper,
                    onError = ::processErrorOpen
                )
            }
        }
    }

    private suspend fun navigateToScreenStreaming(navController: NavHostController) {
        info { "Success open app, then go to screen streaming" }
        withContext(Dispatchers.Main) {
            navController.navigate(screenStreamingFeatureEntry.ROUTE.name)
            openFapState.emit(OpenFapState.Ready)
        }
    }

    private suspend fun processBusyFlipper() {
        info { "Flipper is busy" }
        busyDialogState.emit(true)
        openFapState.emit(OpenFapState.Ready)
    }

    private suspend fun processErrorOpen() {
        info { "Error on try open app" }
        openFapState.emit(OpenFapState.Ready)
    }

    fun closeDialog() {
        viewModelScope.launch {
            busyDialogState.emit(false)
        }
    }
}
