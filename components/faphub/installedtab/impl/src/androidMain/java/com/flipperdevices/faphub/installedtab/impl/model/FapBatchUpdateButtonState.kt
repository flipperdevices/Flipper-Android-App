package com.flipperdevices.faphub.installedtab.impl.model

sealed class FapBatchUpdateButtonState {
    data object Loading : FapBatchUpdateButtonState()

    data class ReadyToUpdate(val count: Int) : FapBatchUpdateButtonState()

    data object Offline : FapBatchUpdateButtonState()
    data object NoUpdates : FapBatchUpdateButtonState()

    data object UpdatingInProgress : FapBatchUpdateButtonState()
}
