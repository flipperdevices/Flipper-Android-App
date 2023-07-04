package com.flipperdevices.faphub.installation.queue.impl.api

import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.ktx.jre.withLockResult
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.impl.executor.FapActionExecutor
import com.flipperdevices.faphub.installation.queue.impl.model.FapInternalQueueState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FapQueueRunner @Inject constructor(
    private val fapActionExecutor: FapActionExecutor
) : LogTagProvider {
    override val TAG = "FapQueueRunner"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val mutex = Mutex()
    private val pendingTaskFlow =
        MutableStateFlow<ImmutableList<FapActionRequest>>(persistentListOf())
    private var currentTaskJob: Job? = null
    private val currentTaskFlow = MutableStateFlow<FapInternalQueueState?>(null)

    init {
        scope.launch {
            pendingTaskFlow.collect { loop() }
        }
    }

    fun pendingTasksFlow() = pendingTaskFlow.asStateFlow()
    fun currentTaskFlow() = currentTaskFlow.asStateFlow()

    fun enqueue(actionRequest: FapActionRequest) = scope.launch {
        enqueueSync(actionRequest)
    }

    suspend fun enqueueSync(actionRequest: FapActionRequest) {
        if (actionRequest is FapActionRequest.Cancel) {
            cancelTasksForApplicationUid(actionRequest)
        } else {
            withLock(mutex, "enqueue") {
                pendingTaskFlow.update { list ->
                    list.filterNot { it.applicationUid == actionRequest.applicationUid }
                        .plus(actionRequest).toImmutableList()
                }
            }
        }
    }

    private suspend fun loop() {
        info { "Start loop" }
        val currentJob = takeJobForLoop() ?: return
        info { "Current job is: $currentJob" }
        currentJob.join()
        info { "Complete current job" }
    }

    private suspend fun takeJobForLoop() = withLockResult(mutex, "create_job") {
        val mutableList = pendingTaskFlow.value.toMutableList()
        val currentTask = mutableList.removeFirstOrNull() ?: return@withLockResult null
        info { "Start execute $currentTask" }
        pendingTaskFlow.emit(mutableList.toImmutableList())
        currentTaskFlow.emit(FapInternalQueueState.Scheduled(currentTask))
        return@withLockResult scope.launch {
            try {
                currentTaskFlow.emit(FapInternalQueueState.InProgress(currentTask, 0f))
                fapActionExecutor.execute(currentTask) { progress ->
                    currentTaskFlow.emit(FapInternalQueueState.InProgress(currentTask, progress))
                }
            } catch (throwable: Throwable) {
                error(throwable) { "While executing $currentTask" }
                currentTaskFlow.emit(FapInternalQueueState.Failed(currentTask, throwable))
            } finally {
                withContext(NonCancellable) {
                    withLock(mutex, "finally_clean") {
                        currentTaskJob = null
                        currentTaskFlow.emit(null)
                    }
                }
            }
        }.also { currentTaskJob = it }
    }

    private suspend fun cancelTasksForApplicationUid(
        actionRequest: FapActionRequest.Cancel
    ) = withLock(mutex, "cancel") {
        info { "Cancel tasks with application uid: $actionRequest" }
        if (currentTaskFlow.value?.request?.applicationUid == actionRequest.applicationUid) {
            info { "Current job is job for canceling, so cancel job" }
            currentTaskJob?.cancel()
        }
        pendingTaskFlow.update { list ->
            list.filterNot { it.applicationUid == actionRequest.applicationUid }.toImmutableList()
        }
    }
}
