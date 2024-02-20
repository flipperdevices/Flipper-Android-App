package com.flipperdevices.faphub.installedtab.impl.model

import com.flipperdevices.faphub.installedtab.impl.viewmodel.FapInstalledInternalLoadingState

sealed class FapBatchUpdateButtonState {
    data object Loading : FapBatchUpdateButtonState()

    data class ReadyToUpdate(val count: Int) : FapBatchUpdateButtonState()

    data object Offline : FapBatchUpdateButtonState()
    data object NoUpdates : FapBatchUpdateButtonState()

    data object UpdatingInProgress : FapBatchUpdateButtonState()
}

fun FapInstalledInternalLoadingState.toButtonState() = when (this) {
    is FapInstalledInternalLoadingState.Error -> FapBatchUpdateButtonState.NoUpdates
    FapInstalledInternalLoadingState.Loading -> FapBatchUpdateButtonState.Loading
    is FapInstalledInternalLoadingState.LoadedOffline -> FapBatchUpdateButtonState.Offline
    is FapInstalledInternalLoadingState.Loaded -> {
        val updatingInProgress = faps.count {
            it.second is FapInstalledInternalState.UpdatingInProgress
        }
        val pendingToUpdate = faps.count {
            it.second is FapInstalledInternalState.ReadyToUpdate
        }
        if (pendingToUpdate > 0) {
            FapBatchUpdateButtonState.ReadyToUpdate(pendingToUpdate)
        } else if (updatingInProgress > 0) {
            FapBatchUpdateButtonState.UpdatingInProgress
        } else {
            FapBatchUpdateButtonState.NoUpdates
        }
    }
}
