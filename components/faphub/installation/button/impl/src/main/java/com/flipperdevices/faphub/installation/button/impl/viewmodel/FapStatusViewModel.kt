package com.flipperdevices.faphub.installation.button.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import kotlinx.coroutines.flow.MutableStateFlow
import tangle.viewmodel.VMInject

class FapStatusViewModel @VMInject constructor(
    private val stateManager: FapInstallationStateManager
) : ViewModel() {
    fun getStateForApplicationId(
        fapButtonConfig: FapButtonConfig?
    ) = if (fapButtonConfig == null) {
        MutableStateFlow(FapState.NotInitialized)
    } else {
        stateManager.getFapStateFlow(
            scope = viewModelScope,
            applicationId = fapButtonConfig.applicationId,
            currentVersion = fapButtonConfig.version.version
        )
    }
}
