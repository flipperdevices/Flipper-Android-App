package com.flipperdevices.updater.card.model

import com.flipperdevices.updater.model.FirmwareVersion

sealed class FlipperUpdateState {
    object NotConnected : FlipperUpdateState()
    object ConnectingInProgress : FlipperUpdateState()
    object Updating : FlipperUpdateState()
    class Complete(val version: FirmwareVersion?) : FlipperUpdateState()
    class Failed(val version: FirmwareVersion?) : FlipperUpdateState()
    object Ready : FlipperUpdateState()
}
