package com.flipperdevices.updater.card.model

sealed class FlipperState {
    object NotReady : FlipperState()
    object Updating : FlipperState()
    object Complete : FlipperState()
    object Failed : FlipperState()
    object Ready : FlipperState()
}
