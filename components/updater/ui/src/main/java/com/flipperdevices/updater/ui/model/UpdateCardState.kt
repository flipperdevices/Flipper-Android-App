package com.flipperdevices.updater.ui.model

import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.FirmwareVersion

sealed class UpdateCardState {
    object InProgress : UpdateCardState()

    data class NoUpdate(
        val flipperVersion: FirmwareVersion
    ) : UpdateCardState()

    data class UpdateAvailable(
        val lastVersion: FirmwareVersion,
        val updaterDist: DistributionFile
    ) : UpdateCardState()

    object Error : UpdateCardState()
}
