package com.flipperdevices.faphub.installation.queue.api

import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.api.model.FapQueueState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface FapInstallationQueueApi {
    fun getFlowById(scope: CoroutineScope, applicationUid: String): StateFlow<FapQueueState>

    fun enqueue(actionRequest: FapActionRequest)
}
