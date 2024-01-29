package com.flipperdevices.updater.model

import androidx.compose.runtime.Immutable

@Immutable
sealed class FlipperUpdateState {
    data object NotConnected : FlipperUpdateState()
    data object ConnectingInProgress : FlipperUpdateState()
    data object Updating : FlipperUpdateState()
    data class Complete(val version: FirmwareVersion?) : FlipperUpdateState()
    data class Failed(val version: FirmwareVersion?) : FlipperUpdateState()
    data object Ready : FlipperUpdateState()
}
