package com.flipperdevices.bridge.synchronization.impl.model

data class KeyDiff(
    val hashedKey: KeyWithHash,
    val action: KeyAction
)
