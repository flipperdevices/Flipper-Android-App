package com.flipperdevices.bridge.synchronization.impl

import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.executor.AndroidKeyStorage
import com.flipperdevices.bridge.synchronization.impl.executor.DiffKeyExecutor
import com.flipperdevices.bridge.synchronization.impl.executor.FlipperKeyStorage
import com.flipperdevices.bridge.synchronization.impl.model.trackProgressAndReturn
import com.flipperdevices.bridge.synchronization.impl.repository.FavoritesRepository
import com.flipperdevices.bridge.synchronization.impl.repository.HashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.KeysListingRepository
import com.flipperdevices.bridge.synchronization.impl.repository.ManifestRepository
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
    reportApi: Shake2ReportApi
) : TaskWithLifecycle(), LogTagProvider {
    override val TAG = "SynchronizationTask"

    private val taskScope = lifecycleScope
    private val diffKeyExecutor = DiffKeyExecutor(reportApi)

    fun start(onStateUpdate: suspend (SynchronizationState) -> Unit) {
        serviceProvider.provideServiceApi(this) { serviceApi ->
            taskScope.launch {
                try {
                    onStateUpdate(SynchronizationState.IN_PROGRESS)
                    launch(serviceApi)
                } catch (exception: Throwable) {
                    error(exception) { "While synchronization we have error" }
                    throw exception
                } finally {
                    onStateUpdate(SynchronizationState.FINISHED)
                    onStop()
                }
            }
        }
        taskScope.launch {
            onStart()
        }
    }

    private suspend fun launch(serviceApi: FlipperServiceApi) = withContext(Dispatchers.Default) {
        // Get keys listing
        val keys = KeysListingRepository().getAllKeys(
            serviceApi.requestApi
        ).trackProgressAndReturn {
            info { "[Keys] Progress is ${it.currentPosition}/${it.maxPosition}: ${it.text}" }
        }
        // Get hashes from Flipper
        val hashes = HashRepository().calculateHash(
            serviceApi.requestApi, keys
        ).trackProgressAndReturn {
            info { "[Hash] Progress is ${it.currentPosition}/${it.maxPosition}: ${it.text}" }
        }

        // Compare hashes with local snapshot
        val repository = ManifestRepository()
        val diffWithFlipper = repository.compareWithManifest(hashes)

        // Apply changes from Flipper
        val flipperStorage = FlipperKeyStorage(serviceApi.requestApi)
        val androidStorage = AndroidKeyStorage(keysApi)
        val appliedKeys = diffKeyExecutor.executeBatch(
            source = flipperStorage,
            target = androidStorage,
            diffs = diffWithFlipper
        )

        info { "[Keys] Successful applied ${appliedKeys.size} from ${diffWithFlipper.size} changes" }

        // End synchronization keys
        repository.saveManifest(hashes)

        val favorites = FavoritesRepository().getFavorites(
            serviceApi.requestApi
        )
        favoriteApi.updateFavorites(favorites)
    }
}
