package com.flipperdevices.screenstreaming.impl.model

sealed class FlipperLockState {
    object NotInitialized : FlipperLockState()
    object NotSupported : FlipperLockState()
    class Ready(val isLocked: Boolean) : FlipperLockState()
}