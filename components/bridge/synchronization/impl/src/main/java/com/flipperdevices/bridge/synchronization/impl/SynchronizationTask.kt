package com.flipperdevices.bridge.synchronization.impl

import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.executor.AndroidKeyStorage
import com.flipperdevices.bridge.synchronization.impl.executor.DiffKeyExecutor
import com.flipperdevices.bridge.synchronization.impl.executor.FlipperKeyStorage
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.bridge.synchronization.impl.model.trackProgressAndReturn
import com.flipperdevices.bridge.synchronization.impl.repository.FavoritesRepository
import com.flipperdevices.bridge.synchronization.impl.repository.HashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.KeysListingRepository
import com.flipperdevices.bridge.synchronization.impl.repository.ManifestRepository
import com.flipperdevices.bridge.synchronization.impl.utils.KeyDiffCombiner
import com.flipperdevices.bridge.synchronization.impl.utils.TaskWithLifecycle
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.wtf
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
        }
        taskScope.launch {
            onStart()
        }
    }

    private suspend fun launch(serviceApi: FlipperServiceApi) = withContext(Dispatchers.Default) {
        val repository = ManifestRepository()
        val flipperStorage = FlipperKeyStorage(serviceApi.requestApi)

        val keysHashes = syncKeys(serviceApi.requestApi, repository, flipperStorage)
        val favorites = syncFavorites(repository, flipperStorage)

        // End synchronization keys
        repository.saveManifest(keysHashes, favorites)
    }

    private suspend fun syncKeys(
        requestApi: FlipperRequestApi,
        manifestRepository: ManifestRepository,
        flipperStorage: FlipperKeyStorage
    ): List<KeyWithHash> {
        // Get keys listing
        val keys = KeysListingRepository().getAllKeys(requestApi).trackProgressAndReturn {
            info { "[Keys] Progress is ${it.currentPosition}/${it.maxPosition}: ${it.text}" }
        }
        // Get hashes from Flipper
        val hashes = HashRepository().calculateHash(requestApi, keys).trackProgressAndReturn {
            info { "[Hash] Progress is ${it.currentPosition}/${it.maxPosition}: ${it.text}" }
        }

        info { "Finish receive hashes: $hashes" }

        // Compare hashes with local snapshot
        val diffWithFlipper = manifestRepository.compareKeysWithManifest(hashes)

        // Apply changes from Flipper
        val androidStorage = AndroidKeyStorage(keysApi)
        val appliedKeys = diffKeyExecutor.executeBatch(
            source = flipperStorage,
            target = androidStorage,
            diffs = diffWithFlipper
        )

        info {
            "[Keys] Successful applied ${appliedKeys.size} from ${diffWithFlipper.size} changes"
        }

        return hashes
    }

    private suspend fun syncFavorites(
        manifestRepository: ManifestRepository,
        flipperStorage: FlipperKeyStorage
    ): List<FlipperKeyPath> {
        val favoritesRepository = FavoritesRepository()
        val favoritesFromFlipper = favoritesRepository.getFavorites(flipperStorage)
        val favoritesFromAndroid = favoriteApi.getFavorites().map { it.path }
        val diffWithManifestAndFlipper = manifestRepository
            .compareFavoritesWithManifest(favoritesFromFlipper)
        val diffWithManifestAndAndroid = manifestRepository
            .compareFavoritesWithManifest(favoritesFromAndroid)

        info {
            "Receive favorites data. " +
                "Flipper: ${favoritesFromFlipper.size} " +
                "(Diff - ${diffWithManifestAndFlipper.size}). " +
                "Android: ${favoritesFromAndroid.size} " +
                "(Diff - ${diffWithManifestAndAndroid.size})"
        }

        val combinedDiff = KeyDiffCombiner.combineKeyDiffs(
            diffWithManifestAndFlipper,
            diffWithManifestAndAndroid
        ) {
            wtf { "Conflict when combine favorites diff with path $it" }
            return@combineKeyDiffs emptyList()
        }
        info { "Favorites diff is $combinedDiff" }

        if (combinedDiff.isEmpty()) {
            return favoritesFromFlipper
        }
        val favoritesFromManifest = manifestRepository.getFavorites() ?: emptyList()
        val resultFavoritesList = ArrayList(favoritesFromManifest)
        for (diff in combinedDiff) {
            when (diff.action) {
                KeyAction.ADD -> resultFavoritesList.add(diff.newHash.keyPath)
                KeyAction.MODIFIED -> resultFavoritesList.add(diff.newHash.keyPath)
                KeyAction.DELETED -> resultFavoritesList.remove(diff.newHash.keyPath)
            }
        }

        info { "Favorites list is $resultFavoritesList" }

        favoritesRepository.updateFavorites(
            flipperStorage,
            resultFavoritesList
        ) // Update on Flipper
        favoriteApi.updateFavorites(resultFavoritesList)

        return resultFavoritesList
    }
}
