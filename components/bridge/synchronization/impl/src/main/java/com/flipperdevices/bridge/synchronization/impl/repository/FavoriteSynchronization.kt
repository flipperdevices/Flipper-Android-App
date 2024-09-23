package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.executor.FlipperKeyStorage
import com.flipperdevices.bridge.synchronization.impl.model.DiffSource
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.FlipperFavoritesRepository
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestRepository
import com.flipperdevices.bridge.synchronization.impl.utils.KeyDiffCombiner
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.DetailedProgressListener
import com.flipperdevices.core.progress.DetailedProgressWrapperTracker
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface FavoriteSynchronization {
    data object FavoritesProgressDetail : DetailedProgressListener.Detail

    suspend fun syncFavorites(progressTracker: DetailedProgressWrapperTracker)
}

@ContributesBinding(TaskGraph::class, FavoriteSynchronization::class)
class FavoriteSynchronizationImpl @Inject constructor(
    private val favoriteApi: FavoriteApi,
    private val manifestRepository: ManifestRepository,
    private val flipperStorage: FlipperKeyStorage,
    private val favoritesRepository: FlipperFavoritesRepository
) : FavoriteSynchronization, LogTagProvider {
    override val TAG = "FavoriteSynchronization"

    override suspend fun syncFavorites(progressTracker: DetailedProgressWrapperTracker) {
        val favoritesFromFlipper = favoritesRepository.getFavorites(flipperStorage)
        progressTracker.onProgress(
            current = 0.5f,
            detail = FavoriteSynchronization.FavoritesProgressDetail
        )
        val favoritesFromAndroid = favoriteApi.getFavorites().map { it.path }
        val diffWithManifestAndFlipper = manifestRepository
            .compareFlipperFavoritesWithManifest(favoritesFromFlipper)
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
        ).sortedBy { it.action }
        info { "Favorites diff is $combinedDiff" }

        if (combinedDiff.isEmpty()) {
            manifestRepository.updateManifest(
                favorites = favoritesFromAndroid,
                favoritesOnFlipper = favoritesFromFlipper
            )
            return
        }

        val diffForFlipper = combinedDiff
            .filter { it.source == DiffSource.ANDROID }
        val newFavoritesOnFlipper = favoritesRepository.applyDiff(
            flipperStorage,
            favoritesFromFlipper,
            diffForFlipper
        ) // Update on Flipper
        val favoritesOnAndroid = favoriteApi.updateFavorites(
            newFavoritesOnFlipper.map {
                FlipperKeyPath(path = it, deleted = false)
            }
        )

        manifestRepository.updateManifest(
            favorites = favoritesOnAndroid.map { it.path },
            favoritesOnFlipper = newFavoritesOnFlipper
        )
        progressTracker.onProgress(
            current = 1f,
            detail = FavoriteSynchronization.FavoritesProgressDetail
        )
    }
}
