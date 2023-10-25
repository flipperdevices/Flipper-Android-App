package com.flipperdevices.faphub.installation.queue.api.model

sealed class FapQueueState {
    data object NotFound : FapQueueState()

    data class Pending(
        val request: FapActionRequest
    ) : FapQueueState()

    data class InProgress(
        val request: FapActionRequest,
        val float: Float
    ) : FapQueueState()

    data class Failed(
        val request: FapActionRequest,
        val throwable: Throwable
    ) : FapQueueState()
}
