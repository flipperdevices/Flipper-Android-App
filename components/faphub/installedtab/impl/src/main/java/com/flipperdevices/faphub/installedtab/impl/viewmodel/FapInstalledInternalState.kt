package com.flipperdevices.faphub.installedtab.impl.viewmodel

import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem

internal sealed class FapInstalledInternalState : Comparable<FapInstalledInternalState> {
    object UpdatingInProgress : FapInstalledInternalState()

    class ReadyToUpdate(
        val manifestItem: FapManifestItem
    ) : FapInstalledInternalState()

    object Installed : FapInstalledInternalState()

    @Suppress("MagicNumber")
    override fun compareTo(other: FapInstalledInternalState): Int {
        return when (this) {
            Installed -> when (other) {
                Installed -> 0
                is ReadyToUpdate -> -1
                UpdatingInProgress -> -2
            }

            is ReadyToUpdate -> when (other) {
                Installed -> +1
                is ReadyToUpdate -> 0
                UpdatingInProgress -> -1
            }

            UpdatingInProgress -> when (other) {
                Installed -> +2
                is ReadyToUpdate -> +1
                UpdatingInProgress -> 0
            }
        }
    }
}
