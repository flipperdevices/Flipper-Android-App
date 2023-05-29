package com.flipperdevices.faphub.installation.queue.impl.model

import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest

sealed class FapInternalQueueState {
    abstract val request: FapActionRequest

    data class Scheduled(
        override val request: FapActionRequest
    ) : FapInternalQueueState()

    data class InProgress(
        override val request: FapActionRequest,
        val float: Float
    ) : FapInternalQueueState()

    data class Failed(
        override val request: FapActionRequest,
        val throwable: Throwable
    ) : FapInternalQueueState()
}