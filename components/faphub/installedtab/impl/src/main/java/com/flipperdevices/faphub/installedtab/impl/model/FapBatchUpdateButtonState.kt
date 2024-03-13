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
    is FapInstalledInternalLoadingState.Loaded -> {
        val uniqueFaps = faps.distinctBy { it.first.applicationUid }
        val updatingInProgress = uniqueFaps.count {
            it.second is FapInstalledInternalState.UpdatingInProgress ||
                it.second is FapInstalledInternalState.UpdatingInProgressActive
        }
        val pendingToUpdate = uniqueFaps.count {
            it.second is FapInstalledInternalState.ReadyToUpdate
        }
        if (updatingInProgress > 0) {
            FapBatchUpdateButtonState.UpdatingInProgress
        } else if (pendingToUpdate > 0) {
            FapBatchUpdateButtonState.ReadyToUpdate(pendingToUpdate)
        } else {
            FapBatchUpdateButtonState.NoUpdates
        }
    }
}
