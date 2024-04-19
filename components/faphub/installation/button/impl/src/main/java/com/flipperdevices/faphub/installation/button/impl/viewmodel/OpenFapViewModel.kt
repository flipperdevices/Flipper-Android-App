package com.flipperdevices.faphub.installation.button.impl.viewmodel

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.impl.helper.OpenFapHelper
import com.flipperdevices.faphub.installation.button.impl.model.OpenFapResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OpenFapViewModel @Inject constructor(
    private val openFapHelper: OpenFapHelper
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "OpenFapViewModel"

    private val busyDialogState = MutableStateFlow(false)
    fun getDialogState() = busyDialogState.asStateFlow()
    fun getOpenFapState(fapButtonConfig: FapButtonConfig?) =
        openFapHelper.getOpenFapState(fapButtonConfig)

    fun open(config: FapButtonConfig?, onOpenScreenStreaming: () -> Unit) {
        if (config == null) {
            info { "Cannot open because config in null" }
            return
        }

        viewModelScope.launch {
            openFapHelper.loadFap(
                config = config,
                onResult = { processOpenFapResult(it, onOpenScreenStreaming) }
            )
        }
    }

    private fun processOpenFapResult(openFapResult: OpenFapResult, onOpenScreenStreaming: () -> Unit) {
        viewModelScope.launch {
            when (openFapResult) {
                OpenFapResult.AllGood -> {
                    info { "Success open app, then go to screen streaming" }
                    withContext(Dispatchers.Main) {
                        onOpenScreenStreaming()
                    }
                }
                OpenFapResult.Error -> info { "Error on open app" }
                OpenFapResult.FlipperIsBusy -> {
                    info { "Flipper is busy" }
                    busyDialogState.emit(true)
                }
            }
        }
    }

    fun closeDialog() {
        viewModelScope.launch {
            busyDialogState.emit(false)
        }
    }
}
