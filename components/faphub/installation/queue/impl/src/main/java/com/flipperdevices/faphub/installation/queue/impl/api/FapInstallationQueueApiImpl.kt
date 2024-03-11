package com.flipperdevices.faphub.installation.queue.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.api.model.FapQueueState
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

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
            val state = if (currentTask != null &&
                currentTask.request.applicationUid == applicationUid
            ) {
                currentTask.toFapQueueState()
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

    override fun getAllTasks(): Flow<ImmutableList<FapQueueState>> {
        return combine(
            queueRunner.currentTaskFlow(),
            queueRunner.pendingTasksFlow()
        ) { currentTask, pendingTasks ->
            val toReturn = mutableListOf<FapQueueState>()
            if (currentTask != null) {
                toReturn.add(currentTask.toFapQueueState())
            }
            toReturn.addAll(pendingTasks.map { FapQueueState.Pending(it) })

            return@combine toReturn.toPersistentList()
        }
    }

    override fun enqueue(actionRequest: FapActionRequest) {
        info { "Enqueue $actionRequest" }
        queueRunner.enqueue(actionRequest)
    }

    override suspend fun enqueueSync(actionRequest: FapActionRequest) {
        info { "Enqueue $actionRequest" }
        queueRunner.enqueueSync(actionRequest)
    }
}
