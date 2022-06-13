package com.flipperdevices.updater.card.model

sealed class FlipperState {
    object NotReady : FlipperState()
    object Updating : FlipperState()
    object Ready : FlipperState()
}
