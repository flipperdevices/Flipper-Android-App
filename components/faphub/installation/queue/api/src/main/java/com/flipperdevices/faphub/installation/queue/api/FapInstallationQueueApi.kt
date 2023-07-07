package com.flipperdevices.faphub.installation.queue.api

import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.api.model.FapQueueState
import kotlinx.coroutines.flow.Flow

interface FapInstallationQueueApi {
    fun getFlowById(applicationUid: String): Flow<FapQueueState>

    fun getAllTasks(): Flow<List<FapQueueState>>

    fun enqueue(actionRequest: FapActionRequest)
    suspend fun enqueueSync(actionRequest: FapActionRequest)
}
