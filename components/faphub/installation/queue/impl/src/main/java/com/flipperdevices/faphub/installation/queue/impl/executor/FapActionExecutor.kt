package com.flipperdevices.faphub.installation.queue.impl.executor

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import javax.inject.Inject

class FapActionExecutor @Inject constructor(
    private val installationExecutor: InstallationActionExecutor
) : LogTagProvider {
    override val TAG = "FapActionExecutor"
    suspend fun execute(
        fapAction: FapActionRequest,
        progressListener: ProgressListener
    ) {
        info { "Received fap action request $fapAction" }
        when (fapAction) {
            is FapActionRequest.Install -> installationExecutor.install(fapAction, progressListener)
            is FapActionRequest.Cancel -> error("Cancel shouldn't be here")
        }
    }
}
