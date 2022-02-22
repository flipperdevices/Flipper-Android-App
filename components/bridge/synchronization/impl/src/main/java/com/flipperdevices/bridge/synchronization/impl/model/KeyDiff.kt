package com.flipperdevices.bridge.synchronization.impl.model

data class KeyDiff constructor(
    val newHash: KeyWithHash,
    val action: KeyAction,
    val source: DiffSource
)

enum class DiffSource {
    ANDROID,
    FLIPPER
}
