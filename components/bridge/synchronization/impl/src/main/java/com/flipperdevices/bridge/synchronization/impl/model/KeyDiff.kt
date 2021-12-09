package com.flipperdevices.bridge.synchronization.impl.model

data class KeyDiff(
    val keyPath: KeyWithHash,
    val action: KeyAction
)
