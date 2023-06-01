package com.flipperdevices.faphub.installation.button.impl.viewmodel

import androidx.lifecycle.ViewModel
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
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
            applicationUid = fapButtonConfig.applicationUid,
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
                toVersion = fapButtonConfig.version,
                categoryAlias = fapButtonConfig.categoryAlias
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
                toVersion = fapButtonConfig.version
            )
        )
    }
}
