package com.flipperdevices.updater.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class UpdateCardState {
    object InProgress : UpdateCardState()

    data class NoUpdate(
        val flipperVersion: FirmwareVersion
    ) : UpdateCardState()

    data class UpdateAvailable(
        val lastVersion: FirmwareVersion,
        val updaterDist: DistributionFile,
        val isOtherChannel: Boolean
    ) : UpdateCardState()

    class Error(
        @DrawableRes val iconId: Int,
        @StringRes val titleId: Int,
        @StringRes val descriptionId: Int
    ) : UpdateCardState()
}
