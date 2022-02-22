package com.flipperdevices.bridge.synchronization.impl

import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.executor.FlipperKeyStorage
import com.flipperdevices.bridge.synchronization.impl.model.RestartSynchronizationException
import com.flipperdevices.bridge.synchronization.impl.repository.FavoriteSynchronization
import com.flipperdevices.bridge.synchronization.impl.repository.KeysSynchronization
import com.flipperdevices.bridge.synchronization.impl.repository.storage.ManifestRepository
import com.flipperdevices.bridge.synchronization.impl.utils.TaskWithLifecycle
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.shake2report.api.Shake2ReportApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SynchronizationTask(
    private val serviceProvider: FlipperServiceProvider,
    private val keysApi: KeyApi,
    private val favoriteApi: FavoriteApi,
    private val reportApi: Shake2ReportApi
) : TaskWithLifecycle(), LogTagProvider {
    override val TAG = "SynchronizationTask"

    private val taskScope = lifecycleScope
    private val manifestRepository = ManifestRepository()

    fun start(onStateUpdate: suspend (SynchronizationState) -> Unit) {
        serviceProvider.provideServiceApi(this) { serviceApi ->
            taskScope.launch {
                startInternal(serviceApi, onStateUpdate)
            }
        }
        taskScope.launch {
            onStart()
        }
    }

    private suspend fun startInternal(
        serviceApi: FlipperServiceApi,
        onStateUpdate: suspend (SynchronizationState) -> Unit
    ) {
        try {
            onStateUpdate(SynchronizationState.IN_PROGRESS)
            launch(serviceApi)
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
            throw exception
        } finally {
            onStateUpdate(SynchronizationState.FINISHED)
            onStop()
        }
    }

    private suspend fun launch(serviceApi: FlipperServiceApi) = withContext(Dispatchers.Default) {
        val flipperStorage = FlipperKeyStorage(serviceApi.requestApi)
        val favoriteSynchronization = FavoriteSynchronization(
            favoriteApi, manifestRepository, flipperStorage
        )
        val keysSynchronization = KeysSynchronization(
            keysApi, manifestRepository, flipperStorage, serviceApi.requestApi, reportApi
        )

        val keysHashes = keysSynchronization.syncKeys()
        val favorites = favoriteSynchronization.syncFavorites()

        // End synchronization keys
        manifestRepository.saveManifest(keysHashes, favorites)
    }
}
