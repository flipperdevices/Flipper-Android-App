package com.flipperdevices.faphub.installation.queue.impl.model

import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.api.model.FapQueueState

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

    fun toFapQueueState() = when (this) {
        is FapInternalQueueState.Failed -> FapQueueState.Failed(
            request,
            throwable
        )

        is FapInternalQueueState.Scheduled -> FapQueueState.Pending(request)
        is FapInternalQueueState.InProgress -> FapQueueState.InProgress(
            request,
            float
        )
    }
}
