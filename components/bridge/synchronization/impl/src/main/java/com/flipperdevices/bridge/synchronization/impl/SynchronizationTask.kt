package com.flipperdevices.bridge.synchronization.impl

import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.FlipperFileApi
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.executor.FlipperKeyStorage
import com.flipperdevices.bridge.synchronization.impl.model.RestartSynchronizationException
import com.flipperdevices.bridge.synchronization.impl.repository.FavoriteSynchronization
import com.flipperdevices.bridge.synchronization.impl.repository.KeysSynchronization
import com.flipperdevices.bridge.synchronization.impl.repository.storage.ManifestRepository
import com.flipperdevices.bridge.synchronization.impl.utils.SynchronizationPercentProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.OneTimeExecutionBleTask
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.SynchronizationEnd
import com.flipperdevices.wearable.sync.handheld.api.SyncWearableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

@Suppress("LongParameterList")
class SynchronizationTask(
    private val serviceProvider: FlipperServiceProvider,
    private val simpleKeyApi: SimpleKeyApi,
    private val deleteKeyApi: DeleteKeyApi,
    private val utilsKeyApi: UtilsKeyApi,
    private val favoriteApi: FavoriteApi,
    private val metricApi: MetricApi,
    private val synchronizationProvider: SynchronizationPercentProvider,
    private val flipperFileApi: FlipperFileApi,
    private val syncWearableApi: SyncWearableApi,
    private val updateKeyApi: UpdateKeyApi
) : OneTimeExecutionBleTask<Unit, SynchronizationState>(serviceProvider), LogTagProvider {
    override val TAG = "SynchronizationTask"

    private val manifestRepository = ManifestRepository()

    override suspend fun startInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        input: Unit,
        stateListener: suspend (SynchronizationState) -> Unit
    ) {
        // Waiting to be connected to the flipper
        serviceApi.connectionInformationApi.getConnectionStateFlow()
            .collectLatest {
                if (it is ConnectionState.Ready &&
                    it.supportedState == FlipperSupportedState.READY
                ) {
                    startInternal(serviceApi, stateListener)
                }
            }
    }

    override suspend fun onStopAsync(stateListener: suspend (SynchronizationState) -> Unit) {
        stateListener(SynchronizationState.Finished)
    }

    private suspend fun startInternal(
        serviceApi: FlipperServiceApi,
        onStateUpdate: suspend (SynchronizationState) -> Unit
    ) {
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
        val flipperStorage = FlipperKeyStorage(serviceApi.requestApi)
        val favoriteSynchronization = FavoriteSynchronization(
            favoriteApi,
            manifestRepository,
            flipperStorage
        )
        val keysSynchronization = KeysSynchronization(
            simpleKeyApi,
            deleteKeyApi,
            utilsKeyApi,
            manifestRepository,
            flipperStorage,
            serviceApi.requestApi,
            synchronizationProvider,
            flipperFileApi,
            updateKeyApi
        )

        val keysHashes = keysSynchronization.syncKeys(onStateUpdate)
        val favorites = favoriteSynchronization.syncFavorites()

        // End synchronization keys
        manifestRepository.saveManifest(keysHashes, favorites)
        synchronizationProvider.markedAsFinish()
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
