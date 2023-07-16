package com.flipperdevices.faphub.installation.button.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.impl.helper.OpenFapHelper
import com.flipperdevices.screenstreaming.api.ScreenStreamingFeatureEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tangle.viewmodel.VMInject

class OpenFapViewModel @VMInject constructor(
    private val openFapHelper: OpenFapHelper,
    private val screenStreamingFeatureEntry: ScreenStreamingFeatureEntry,
) : ViewModel(), LogTagProvider {
    override val TAG: String = "OpenFapViewModel"

    private val busyDialogState = MutableStateFlow(false)
    fun getDialogState() = busyDialogState.asStateFlow()
    fun getOpenFapState(fapButtonConfig: FapButtonConfig?) =
        openFapHelper.getOpenFapState(fapButtonConfig)

    fun open(config: FapButtonConfig?, navController: NavHostController) {
        if (config == null) {
            info { "Cannot open because config in null" }
            return
        }

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                openFapHelper.loadFap(
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
        }
    }

    private suspend fun processBusyFlipper() {
        info { "Flipper is busy" }
        busyDialogState.emit(true)
    }

    private fun processErrorOpen() {
        info { "Error on try open app" }
    }

    fun closeDialog() {
        viewModelScope.launch {
            busyDialogState.emit(false)
        }
    }
}
