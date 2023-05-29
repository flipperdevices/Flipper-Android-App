package com.flipperdevices.faphub.installation.queue.impl.executor

import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.faphub.installation.queue.impl.model.FapActionRequest
import javax.inject.Inject

class FapActionExecutor @Inject constructor(
    private val installationExecutor: InstallationActionExecutor
) {
    suspend fun execute(
        fapAction: FapActionRequest,
        progressListener: ProgressListener
    ) {
        when (fapAction) {
            is FapActionRequest.Install -> installationExecutor.install(fapAction, progressListener)
        }
    }
}
