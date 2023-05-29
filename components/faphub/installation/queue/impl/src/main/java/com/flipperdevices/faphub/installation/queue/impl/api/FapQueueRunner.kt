package com.flipperdevices.faphub.installation.queue.impl.api

import com.flipperdevices.faphub.installation.queue.impl.model.FapActionRequest
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class FapQueueRunner @Inject constructor() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun enqueue(actionRequest: FapActionRequest) {

    }
}