package com.flipperdevices.faphub.installedtab.impl.model

sealed class FapBatchUpdateButtonState {
    object Loading : FapBatchUpdateButtonState()

    data class ReadyToUpdate(val count: Int) : FapBatchUpdateButtonState()

    object Offline : FapBatchUpdateButtonState()
    object NoUpdates : FapBatchUpdateButtonState()

    object UpdatingInProgress : FapBatchUpdateButtonState()
}
