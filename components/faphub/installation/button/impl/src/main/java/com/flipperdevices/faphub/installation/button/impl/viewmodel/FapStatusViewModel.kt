package com.flipperdevices.faphub.installation.button.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import kotlinx.coroutines.flow.MutableStateFlow
import tangle.viewmodel.VMInject

class FapStatusViewModel @VMInject constructor(
    private val stateManager: FapInstallationStateManager,
    private val queueApi: FapInstallationQueueApi
) : ViewModel() {
    fun getStateForApplicationId(
        fapButtonConfig: FapButtonConfig?
    ) = if (fapButtonConfig == null) {
        MutableStateFlow(FapState.NotInitialized)
    } else {
        stateManager.getFapStateFlow(
            scope = viewModelScope,
            applicationUid = fapButtonConfig.applicationAlias,
            currentVersion = fapButtonConfig.version.version
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
                toVersionId = fapButtonConfig.version.id,
                categoryAlias = fapButtonConfig.categoryAlias
            )
        )
    }
}
