package com.flipperdevices.faphub.installation.button.impl.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.impl.helper.LoadFapHelper
import com.flipperdevices.faphub.installation.button.impl.model.OpenFapState
import com.flipperdevices.screenstreaming.api.ScreenStreamingFeatureEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tangle.viewmodel.VMInject

class OpenFapViewModel @VMInject constructor(
    private val loadFapHelper: LoadFapHelper,
    private val screenStreamingFeatureEntry: ScreenStreamingFeatureEntry,
    private val serviceProvider: FlipperServiceProvider,
) : LifecycleViewModel() {

    private val busyDialogState = MutableStateFlow(false)
    fun getDialogState() = busyDialogState.asStateFlow()

    private val openFapState = MutableStateFlow<OpenFapState>(OpenFapState.NotSupported)
    fun getOpenFapState(config: FapButtonConfig?): StateFlow<OpenFapState> {
        if (config == null) {
            return MutableStateFlow(OpenFapState.NotSupported).asStateFlow()
        }

        return openFapState.map { state ->
            when (state) {
                is OpenFapState.InProgress -> {
                    if (state.config == config) {
                        OpenFapState.InProgress(config)
                    } else {
                        OpenFapState.Ready
                    }
                }
                else -> state
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = OpenFapState.NotSupported,
        )
    }

    fun open(config: FapButtonConfig?, navController: NavHostController) {
        if (config == null) {
            return
        }

        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                openFapState.emit(OpenFapState.InProgress(config))
            }

            withContext(Dispatchers.Default) {
                val serviceApi = serviceProvider.getServiceApi()

                loadFapHelper.loadFap(
                    serviceApi = serviceApi,
                    config = config,
                    onSuccess = { navigateToScreenStreaming(navController) },
                    onBusy = { busyDialogState.emit(true) },
                    onError = { openFapState.emit(OpenFapState.Ready) }
                )
            }
        }
    }

    private suspend fun navigateToScreenStreaming(navController: NavHostController) {
        withContext(Dispatchers.Main) {
            navController.navigate(screenStreamingFeatureEntry.ROUTE.name)
        }
    }

    fun closeDialog() {
        viewModelScope.launch {
            busyDialogState.emit(false)
        }
    }
}
