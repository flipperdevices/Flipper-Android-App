package com.flipperdevices.share.receive.model

import com.flipperdevices.deeplink.model.Deeplink

sealed class ReceiveState {
    data class Pending(val deeplink: Deeplink) : ReceiveState()

    data class Saving(val deeplink: Deeplink) : ReceiveState()

    object Finished : ReceiveState()
}
