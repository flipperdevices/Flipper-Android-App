package com.flipperdevices.share.receive.model

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.share.api.ShareContentError

sealed class ReceiveState {
    object NotStarted : ReceiveState()

    data class Pending(
        val flipperKey: FlipperKey,
        val parsed: FlipperKeyParsed
    ) : ReceiveState()

    data class Saving(
        val flipperKey: FlipperKey,
        val parsed: FlipperKeyParsed
    ) : ReceiveState()

    object Finished : ReceiveState()
    data class Error(val type: ShareContentError) : ReceiveState()
}
