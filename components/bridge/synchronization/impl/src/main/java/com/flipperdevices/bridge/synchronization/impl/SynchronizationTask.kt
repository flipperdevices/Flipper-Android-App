package com.flipperdevices.bridge.synchronization.impl

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.di.TaskSynchronizationComponent
import com.flipperdevices.bridge.synchronization.impl.executor.DiffKeyExecutor
import com.flipperdevices.bridge.synchronization.impl.model.RestartSynchronizationException
import com.flipperdevices.bridge.synchronization.impl.repository.FavoriteSynchronization
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.FlipperHashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.TimestampSynchronizationChecker
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.DetailedProgressListener
import com.flipperdevices.core.progress.DetailedProgressWrapperTracker
import com.flipperdevices.core.ui.lifecycle.OneTimeExecutionBleTask
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.SynchronizationEnd
import com.flipperdevices.nfc.mfkey32.api.MfKey32Api
import com.flipperdevices.wearable.sync.handheld.api.SyncWearableApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SynchronizationTask {

    data object SynchronizationStartedProgressDetail : DetailedProgressListener.Detail
    data object SynchronizationFinishedProgressDetail : DetailedProgressListener.Detail

    fun start(
        input: Unit,
        stateListener: suspend (SynchronizationState) -> Unit
    ): Job

    suspend fun onStop()

    interface Builder {
        fun build(): SynchronizationTask
    }
}

@ContributesBinding(AppGraph::class, SynchronizationTask.Builder::class)
class SynchronizationTaskBuilder @Inject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val simpleKeyApi: SimpleKeyApi,
    private val metricApi: MetricApi,
    private val syncWearableApi: SyncWearableApi,
    private val mfKey32Api: MfKey32Api
) : SynchronizationTask.Builder {
    override fun build(): SynchronizationTask {
        return SynchronizationTaskImpl(
            serviceProvider,
            simpleKeyApi,
            metricApi,
            syncWearableApi,
            mfKey32Api
        )
    }
}

private const val START_SYNCHRONIZATION_PERCENT = 0.01f

class SynchronizationTaskImpl(
    serviceProvider: FlipperServiceProvider,
    private val simpleKeyApi: SimpleKeyApi,
    private val metricApi: MetricApi,
    private val syncWearableApi: SyncWearableApi,
    private val mfKey32Api: MfKey32Api
) : OneTimeExecutionBleTask<Unit, SynchronizationState>(serviceProvider),
    SynchronizationTask,
    LogTagProvider {
    override val TAG = "SynchronizationTask"

    private fun mapState(
        detail: DetailedProgressListener.Detail?,
        progress: Float,
    ): SynchronizationState.InProgress {
        return when (detail) {
            is DiffKeyExecutor.DiffProgressDetail -> {
                SynchronizationState.InProgress.FileInProgress(
                    progress = progress,
                    fileName = detail.fileName,
                )
            }

            is TimestampSynchronizationChecker.TimestampsProgressDetail -> {
                SynchronizationState.InProgress.Prepare(
                    progress = progress,
                )
            }

            is FavoriteSynchronization.FavoritesProgressDetail -> {
                SynchronizationState.InProgress.Favorites(
                    progress = progress,
                )
            }

            is FlipperHashRepository.HashesProgressDetail -> {
                SynchronizationState.InProgress.PrepareHashes(
                    progress = progress,
                    keyType = detail.keyType
                )
            }

            else -> SynchronizationState.InProgress.Default(
                progress = progress,
            )
        }
    }

    override suspend fun startInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        input: Unit,
        stateListener: suspend (SynchronizationState) -> Unit
    ) {
        // Waiting to be connected to the flipper
        serviceApi.connectionInformationApi.getConnectionStateFlow()
            .filter {
                it is ConnectionState.Ready &&
                    it.supportedState == FlipperSupportedState.READY
            }.first()
        startInternal(
            scope,
            serviceApi,
            DetailedProgressWrapperTracker(
                min = 0f,
                max = 1f,
                progressListener = { progress, detail ->
                    val state = mapState(detail, progress)
                    stateListener(state)
                }
            )
        )
        stateListener(SynchronizationState.Finished)
    }

    override suspend fun onStopAsync(stateListener: suspend (SynchronizationState) -> Unit) {
        stateListener(SynchronizationState.Finished)
    }

    private suspend fun startInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        progressTracker: DetailedProgressWrapperTracker
    ) {
        info { "#startInternal" }
        val mfKey32check = scope.async { checkMfKey32Safe(serviceApi.requestApi) }
        try {
            progressTracker.onProgress(
                current = START_SYNCHRONIZATION_PERCENT,
                detail = SynchronizationTask.SynchronizationStartedProgressDetail
            )
            launch(
                serviceApi,
                DetailedProgressWrapperTracker(
                    min = START_SYNCHRONIZATION_PERCENT,
                    max = 1.0f,
                    progressListener = progressTracker
                )
            )
            progressTracker.onProgress(
                current = 1.0f,
                detail = SynchronizationTask.SynchronizationFinishedProgressDetail
            )
            mfKey32check.await()
        } catch (
            @Suppress("SwallowedException")
            restartException: RestartSynchronizationException
        ) {
            info { "Synchronization request restart" }
            startInternal(scope, serviceApi, progressTracker)
        }
    }

    private suspend fun checkMfKey32Safe(requestApi: FlipperRequestApi) = try {
        mfKey32Api.checkBruteforceFileExist(requestApi)
    } catch (throwable: Throwable) {
        error(throwable) { "Failed check mfkey32" }
    }

    private suspend fun launch(
        serviceApi: FlipperServiceApi,
        progressTracker: DetailedProgressWrapperTracker
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        val startSynchronizationTime = System.currentTimeMillis()
        val taskComponent = TaskSynchronizationComponent.ManualFactory
            .create(
                ComponentHolder.component(),
                serviceApi.requestApi,
                serviceApi.flipperVersionApi
            )

        val keysChanged = taskComponent.keysSynchronization.syncKeys(
            DetailedProgressWrapperTracker(
                min = 0f,
                max = 0.90f,
                progressListener = progressTracker
            )
        )
        taskComponent.favoriteSynchronization.syncFavorites(
            DetailedProgressWrapperTracker(
                min = 0.90f,
                max = 0.95f,
                progressListener = progressTracker
            )
        )
        val endSynchronizationTime = System.currentTimeMillis() - startSynchronizationTime
        reportSynchronizationEnd(endSynchronizationTime, keysChanged)

        try {
            syncWearableApi.updateWearableIndex()
        } catch (throwable: Exception) {
            error(throwable) { "Error while try update wearable index" }
        }
        progressTracker.onProgress(1.0f, SynchronizationTask.SynchronizationFinishedProgressDetail)
    }

    private suspend fun reportSynchronizationEnd(totalTime: Long, keysChanged: Int) {
        val keys = simpleKeyApi.getAllKeys().groupBy { it.path.keyType }
        metricApi.reportComplexEvent(
            SynchronizationEnd(
                subghzCount = keys[FlipperKeyType.SUB_GHZ]?.size ?: 0,
                rfidCount = keys[FlipperKeyType.RFID]?.size ?: 0,
                nfcCount = keys[FlipperKeyType.NFC]?.size ?: 0,
                infraredCount = keys[FlipperKeyType.INFRARED]?.size ?: 0,
                iButtonCount = keys[FlipperKeyType.I_BUTTON]?.size ?: 0,
                synchronizationTimeMs = totalTime,
                changesCount = keysChanged
            )
        )
    }
}
