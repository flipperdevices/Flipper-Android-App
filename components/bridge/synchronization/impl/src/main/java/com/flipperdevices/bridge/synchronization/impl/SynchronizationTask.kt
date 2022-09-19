package com.flipperdevices.bridge.synchronization.impl

import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.di.DaggerTaskSynchronizationComponent
import com.flipperdevices.bridge.synchronization.impl.model.RestartSynchronizationException
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.OneTimeExecutionBleTask
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.SynchronizationEnd
import com.flipperdevices.wearable.sync.handheld.api.SyncWearableApi
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

interface SynchronizationTask {
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
    private val syncWearableApi: SyncWearableApi
) : SynchronizationTask.Builder {
    override fun build(): SynchronizationTask {
        return SynchronizationTaskImpl(serviceProvider, simpleKeyApi, metricApi, syncWearableApi)
    }
}

class SynchronizationTaskImpl @Inject constructor(
    serviceProvider: FlipperServiceProvider,
    private val simpleKeyApi: SimpleKeyApi,
    private val metricApi: MetricApi,
    private val syncWearableApi: SyncWearableApi
) : OneTimeExecutionBleTask<Unit, SynchronizationState>(serviceProvider),
    SynchronizationTask,
    LogTagProvider {
    override val TAG = "SynchronizationTask"

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
        startInternal(serviceApi, stateListener)
    }

    override suspend fun onStopAsync(stateListener: suspend (SynchronizationState) -> Unit) {
        stateListener(SynchronizationState.Finished)
    }

    private suspend fun startInternal(
        serviceApi: FlipperServiceApi,
        onStateUpdate: suspend (SynchronizationState) -> Unit
    ) {
        info { "#startInternal" }

        try {
            onStateUpdate(SynchronizationState.InProgress(0f))
            val startSynchronizationTime = System.currentTimeMillis()
            launch(serviceApi, onStateUpdate)
            val endSynchronizationTime = System.currentTimeMillis() - startSynchronizationTime
            reportSynchronizationEnd(endSynchronizationTime)
            onStateUpdate(SynchronizationState.Finished)
        } catch (
            @Suppress("SwallowedException")
            restartException: RestartSynchronizationException
        ) {
            info { "Synchronization request restart" }
            startInternal(serviceApi, onStateUpdate)
        }
    }

    private suspend fun launch(
        serviceApi: FlipperServiceApi,
        onStateUpdate: suspend (SynchronizationState) -> Unit
    ) = withContext(Dispatchers.Default) {
        val taskComponent = DaggerTaskSynchronizationComponent.factory()
            .create(
                ComponentHolder.component(),
                serviceApi.requestApi
            )

        val keysHashes = taskComponent.keysSynchronization.syncKeys(onStateUpdate)
        val favorites = taskComponent.favoriteSynchronization.syncFavorites()

        // End synchronization keys
        taskComponent.manifestRepository.saveManifest(keysHashes, favorites)
        taskComponent.synchronizationProvider.markedAsFinish()

        try {
            syncWearableApi.updateWearableIndex()
        } catch (throwable: Exception) {
            error(throwable) { "Error while try update wearable index" }
        }
    }

    private suspend fun reportSynchronizationEnd(totalTime: Long) {
        val keys = simpleKeyApi.getAllKeys().groupBy { it.path.keyType }
        metricApi.reportComplexEvent(
            SynchronizationEnd(
                subghzCount = keys[FlipperKeyType.SUB_GHZ]?.size ?: 0,
                rfidCount = keys[FlipperKeyType.RFID]?.size ?: 0,
                nfcCount = keys[FlipperKeyType.NFC]?.size ?: 0,
                infraredCount = keys[FlipperKeyType.INFRARED]?.size ?: 0,
                iButtonCount = keys[FlipperKeyType.I_BUTTON]?.size ?: 0,
                synchronizationTimeMs = totalTime
            )
        )
    }
}
