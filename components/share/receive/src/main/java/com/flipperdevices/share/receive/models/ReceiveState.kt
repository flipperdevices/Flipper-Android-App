package com.flipperdevices.share.receive.models

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed

sealed class ReceiveState {
    data object NotStarted : ReceiveState()

    data class Pending(
        val flipperKey: FlipperKey,
        val parsed: FlipperKeyParsed,
        val isSaving: Boolean = false
    ) : ReceiveState()

    data object Finished : ReceiveState()
    data class Error(val type: ReceiverError) : ReceiveState()
}
