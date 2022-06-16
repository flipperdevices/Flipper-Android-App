package com.flipperdevices.updater.card.model

import com.flipperdevices.updater.model.FirmwareVersion

sealed class FlipperState {
    object NotReady : FlipperState()
    object Updating : FlipperState()
    class Complete(val version: FirmwareVersion?) : FlipperState()
    class Failed(val version: FirmwareVersion?) : FlipperState()
    object Ready : FlipperState()
}
