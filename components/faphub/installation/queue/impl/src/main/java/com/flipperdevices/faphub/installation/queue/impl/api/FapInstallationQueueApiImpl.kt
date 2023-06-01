package com.flipperdevices.faphub.installation.queue.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.api.model.FapQueueState
import com.flipperdevices.faphub.installation.queue.impl.model.FapInternalQueueState
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@Singleton
@ContributesBinding(AppGraph::class, FapInstallationQueueApi::class)
class FapInstallationQueueApiImpl @Inject constructor(
    private val queueRunner: FapQueueRunner
) : FapInstallationQueueApi, LogTagProvider {
    override val TAG = "FapInstallationQueueApi"

    override fun getFlowById(
        applicationUid: String
    ): Flow<FapQueueState> {
        return combine(
            queueRunner.currentTaskFlow(),
            queueRunner.pendingTasksFlow()
        ) { currentTask, pendingTasks ->
            val state =
                if (currentTask != null && currentTask.request.applicationUid == applicationUid) {
                    when (currentTask) {
                        is FapInternalQueueState.Failed -> FapQueueState.Failed(
                            currentTask.request,
                            currentTask.throwable
                        )

                        is FapInternalQueueState.Scheduled -> FapQueueState.Pending(currentTask.request)
                        is FapInternalQueueState.InProgress -> FapQueueState.InProgress(
                            currentTask.request,
                            currentTask.float
                        )
                    }
                } else {
                    val task = pendingTasks.find { it.applicationUid == applicationUid }
                    if (task != null) {
                        FapQueueState.Pending(task)
                    } else {
                        FapQueueState.NotFound
                    }
                }
            return@combine state
        }
    }

    override fun enqueue(actionRequest: FapActionRequest) {
        queueRunner.enqueue(actionRequest)
    }
}
