package com.flipperdevices.bridge.synchronization.impl

import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.executor.FlipperKeyStorage
import com.flipperdevices.bridge.synchronization.impl.model.RestartSynchronizationException
import com.flipperdevices.bridge.synchronization.impl.repository.FavoriteSynchronization
import com.flipperdevices.bridge.synchronization.impl.repository.KeysSynchronization
import com.flipperdevices.bridge.synchronization.impl.repository.storage.ManifestRepository
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.TaskWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

class SynchronizationTask(
    private val serviceProvider: FlipperServiceProvider,
    private val simpleKeyApi: SimpleKeyApi,
    private val deleteKeyApi: DeleteKeyApi,
    private val utilsKeyApi: UtilsKeyApi,
    private val favoriteApi: FavoriteApi
) : TaskWithLifecycle(), LogTagProvider {
    override val TAG = "SynchronizationTask"

    private val taskScope = lifecycleScope
    private val mutex = Mutex()
    private val manifestRepository = ManifestRepository()
    private var synchronizationJob: Job? = null

    fun start(
        onStateUpdate: suspend (SynchronizationState) -> Unit
    ) = taskScope.launch(Dispatchers.Main) {
        info { "Start synchronization" }
        serviceProvider.provideServiceApi(this@SynchronizationTask) { serviceApi ->
            info { "Flipper service provided" }
            launchWithLock(mutex, taskScope) {
                synchronizationJob?.cancelAndJoin()
                synchronizationJob = null
                synchronizationJob = taskScope.launch {
                    // Waiting to be connected to the flipper
                    serviceApi.connectionInformationApi.getConnectionStateFlow()
                        .collectLatest {
                            if (it is ConnectionState.Ready && it.isSupported) {
                                startInternal(serviceApi, onStateUpdate)
                            }
                        }
                }
            }
        }
        onStart()
        taskScope.launch(Dispatchers.Default) {
            try {
                awaitCancellation()
            } finally {
                withContext(NonCancellable) {
                    onStateUpdate(SynchronizationState.Finished)
                }
            }
        }
    }

    private suspend fun startInternal(
        serviceApi: FlipperServiceApi,
        onStateUpdate: suspend (SynchronizationState) -> Unit
    ) {
        try {
            onStateUpdate(SynchronizationState.InProgress(0f))
            launch(serviceApi, onStateUpdate)
            onStateUpdate(SynchronizationState.Finished)
        } catch (
            @Suppress("SwallowedException")
            restartException: RestartSynchronizationException
        ) {
            info { "Synchronization request restart" }
            startInternal(serviceApi, onStateUpdate)
        } catch (
            @Suppress("detekt:TooGenericExceptionCaught")
            exception: Throwable
        ) {
            error(exception) { "While synchronization we have error" }
            withContext(Dispatchers.Main) {
                onStop()
            }
        }
    }

    private suspend fun launch(
        serviceApi: FlipperServiceApi,
        onStateUpdate: suspend (SynchronizationState) -> Unit
    ) = withContext(Dispatchers.Default) {
        val flipperStorage = FlipperKeyStorage(serviceApi.requestApi)
        val favoriteSynchronization = FavoriteSynchronization(
            favoriteApi, manifestRepository, flipperStorage
        )
        val keysSynchronization = KeysSynchronization(
            simpleKeyApi,
            deleteKeyApi,
            utilsKeyApi,
            manifestRepository,
            flipperStorage,
            serviceApi.requestApi
        )

        val keysHashes = keysSynchronization.syncKeys(onStateUpdate)
        val favorites = favoriteSynchronization.syncFavorites()

        // End synchronization keys
        manifestRepository.saveManifest(keysHashes, favorites)
    }
}
