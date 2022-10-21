package com.flipperdevices.updater.model

sealed class FlipperUpdateState {
    object NotConnected : FlipperUpdateState()
    object ConnectingInProgress : FlipperUpdateState()
    object Updating : FlipperUpdateState()
    class Complete(val version: FirmwareVersion?) : FlipperUpdateState()
    class Failed(val version: FirmwareVersion?) : FlipperUpdateState()
    object Ready : FlipperUpdateState()
}
