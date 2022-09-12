package com.flipperdevices.wearable.emulate.impl.model

import androidx.compose.runtime.Stable

@Stable
sealed class WearEmulateState {
    object Loading : WearEmulateState()
    object NotFoundNode : WearEmulateState()

    @Stable
    data class FoundNode(val nodeId: String) : WearEmulateState()

    @Stable
    data class Emulating(val nodeId: String) : WearEmulateState()
}
