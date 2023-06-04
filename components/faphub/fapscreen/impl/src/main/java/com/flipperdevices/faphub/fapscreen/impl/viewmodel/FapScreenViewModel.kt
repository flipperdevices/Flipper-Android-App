package com.flipperdevices.faphub.fapscreen.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.fapscreen.impl.api.FAP_ID_KEY
import com.flipperdevices.faphub.fapscreen.impl.model.FapDetailedControlState
import com.flipperdevices.faphub.fapscreen.impl.model.FapScreenLoadingState
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

class FapScreenViewModel @VMInject constructor(
    @TangleParam(FAP_ID_KEY)
    private val fapId: String,
    private val fapNetworkApi: FapNetworkApi,
    private val stateManager: FapInstallationStateManager,
    private val fapQueueApi: FapInstallationQueueApi
) : ViewModel(), LogTagProvider {
    override val TAG = "FapScreenViewModel"
    private val mutex = Mutex()

    private val fapScreenLoadingStateFlow = MutableStateFlow<FapScreenLoadingState>(
        FapScreenLoadingState.Loading
    )

    private val controlStateFlow = MutableStateFlow<FapDetailedControlState>(
        FapDetailedControlState.Loading
    )

    private var controlStateJob: Job? = null

    init {
        onRefresh()
    }

    fun getLoadingState() = fapScreenLoadingStateFlow.asStateFlow()

    fun getControlState() = controlStateFlow.asStateFlow()

    fun onDelete() {
        fapQueueApi.enqueue(FapActionRequest.Delete(fapId))
    }

    fun onRefresh() = launchWithLock(mutex, viewModelScope, "refresh") {
        fapNetworkApi.getFapItemById(fapId).onSuccess { fapItem ->
            fapScreenLoadingStateFlow.emit(FapScreenLoadingState.Loaded(fapItem))
            controlStateJob?.cancelAndJoin()
            controlStateJob = stateManager.getFapStateFlow(
                applicationUid = fapId,
                currentVersion = fapItem.upToDateVersion.version
            ).onEach { state ->
                controlStateFlow.emit(state.toControlState(fapItem))
            }.launchIn(viewModelScope)
        }.onFailure {
            error(it) { "Failed fetch single application" }
            fapScreenLoadingStateFlow.emit(FapScreenLoadingState.Error(it))
        }
    }

    private fun FapState.toControlState(fapItem: FapItem): FapDetailedControlState = when (this) {
        FapState.Canceling,
        FapState.Deleting,
        FapState.RetrievingManifest,
        FapState.NotInitialized -> FapDetailedControlState.Loading

        is FapState.InstallationInProgress,
        is FapState.UpdatingInProgress,
        FapState.ReadyToInstall -> FapDetailedControlState.InProgressOrNotInstalled(
            fapItem,
            generateUrl(fapItem.applicationAlias)
        )

        FapState.Installed,
        is FapState.ReadyToUpdate -> FapDetailedControlState.Installed(
            fapItem,
            generateUrl(fapItem.applicationAlias)
        )
    }

    private fun generateUrl(alias: String) = "https://lab.flipp.dev/apps/$alias"
}
