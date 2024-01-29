package com.flipperdevices.faphub.installedtab.impl.model

import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem

sealed class FapInstalledInternalState(val order: Int) :
    Comparable<FapInstalledInternalState> {
    object InstallingInProgressActive : FapInstalledInternalState(order = 3)

    object InstallingInProgress : FapInstalledInternalState(order = 2)
    object UpdatingInProgressActive : FapInstalledInternalState(order = 3)

    object UpdatingInProgress : FapInstalledInternalState(order = 2)

    class ReadyToUpdate(
        val manifestItem: FapManifestItem
    ) : FapInstalledInternalState(order = 1)

    object Installed : FapInstalledInternalState(order = 0)

    override fun compareTo(other: FapInstalledInternalState): Int {
        return other.order - this.order
    }
}
