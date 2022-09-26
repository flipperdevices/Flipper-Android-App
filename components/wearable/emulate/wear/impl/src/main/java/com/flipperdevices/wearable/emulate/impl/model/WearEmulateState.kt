package com.flipperdevices.wearable.emulate.impl.model

import androidx.compose.runtime.Stable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType

@Stable
sealed class WearEmulateState {
    abstract val keyType: FlipperKeyType?

    @Stable
    data class ConnectingToPhone(override val keyType: FlipperKeyType?) : WearEmulateState()

    @Stable
    data class NotFoundNode(override val keyType: FlipperKeyType?) : WearEmulateState()

    @Stable
    data class FoundNode(
        override val keyType: FlipperKeyType?,
        val nodeId: String
    ) : WearEmulateState()

    @Stable
    data class Emulating(
        override val keyType: FlipperKeyType?,
        val nodeId: String
    ) : WearEmulateState()
}
