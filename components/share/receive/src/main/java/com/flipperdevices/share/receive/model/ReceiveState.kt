package com.flipperdevices.share.receive.model

import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.deeplink.model.Deeplink

sealed class ReceiveState {
    object NotStarted : ReceiveState()

    data class Pending(
        val deeplink: Deeplink,
        val parsed: FlipperKeyParsed
    ) : ReceiveState()

    data class Saving(
        val deeplink: Deeplink,
        val parsed: FlipperKeyParsed
    ) : ReceiveState()

    object Finished : ReceiveState()
}
