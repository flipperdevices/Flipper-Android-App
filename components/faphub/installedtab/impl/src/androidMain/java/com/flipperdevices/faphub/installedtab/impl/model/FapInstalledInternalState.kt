package com.flipperdevices.faphub.installedtab.impl.model

import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem

sealed class FapInstalledInternalState(val order: Int) :
    Comparable<FapInstalledInternalState> {
    data object InstallingInProgressActive : FapInstalledInternalState(order = 3)

    data object InstallingInProgress : FapInstalledInternalState(order = 2)
    data object UpdatingInProgressActive : FapInstalledInternalState(order = 3)

    data object UpdatingInProgress : FapInstalledInternalState(order = 2)

    data class ReadyToUpdate(
        val manifestItem: FapManifestItem
    ) : FapInstalledInternalState(order = 1)

    data object Installed : FapInstalledInternalState(order = 0)

    override fun compareTo(other: FapInstalledInternalState): Int {
        return other.order - this.order
    }
}
