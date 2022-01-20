package com.flipperdevices.bridge.synchronization.impl.model

data class KeyDiff(
    val newHash: KeyWithHash,
    val action: KeyAction
)
