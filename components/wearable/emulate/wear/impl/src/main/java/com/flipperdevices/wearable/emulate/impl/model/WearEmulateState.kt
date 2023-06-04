package com.flipperdevices.wearable.emulate.impl.model

import androidx.compose.runtime.Stable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.keyemulate.model.EmulateProgress

@Stable
sealed class WearEmulateState {
    open val keyType: FlipperKeyType? = null

    object NotInitialized : WearEmulateState()

    object NodeFinding : WearEmulateState()

    @Stable
    data class EstablishConnection(
        val nodeId: String
    ) : WearEmulateState()

    object NotFoundNode : WearEmulateState()

    object TestConnection : WearEmulateState()

    object ConnectingToFlipper : WearEmulateState()

    object UnsupportedFlipper : WearEmulateState()

    @Stable
    data class ReadyForEmulate(
        override val keyType: FlipperKeyType?
    ) : WearEmulateState()

    @Stable
    data class Emulating(
        override val keyType: FlipperKeyType?,
        val progress: EmulateProgress
    ) : WearEmulateState()
}
