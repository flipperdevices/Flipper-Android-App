package com.flipperdevices.bridge.synchronization.impl.di

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.service.FlipperVersionApi
import com.flipperdevices.bridge.synchronization.impl.executor.AndroidKeyStorage
import com.flipperdevices.bridge.synchronization.impl.executor.DiffKeyExecutorImpl
import com.flipperdevices.bridge.synchronization.impl.executor.FlipperKeyStorage
import com.flipperdevices.bridge.synchronization.impl.repository.FavoriteSynchronization
import com.flipperdevices.bridge.synchronization.impl.repository.FavoriteSynchronizationImpl
import com.flipperdevices.bridge.synchronization.impl.repository.FolderKeySynchronizationImpl
import com.flipperdevices.bridge.synchronization.impl.repository.KeyDiffApplierImpl
import com.flipperdevices.bridge.synchronization.impl.repository.KeysSynchronization
import com.flipperdevices.bridge.synchronization.impl.repository.KeysSynchronizationImpl
import com.flipperdevices.bridge.synchronization.impl.repository.android.AndroidHashRepositoryImpl
import com.flipperdevices.bridge.synchronization.impl.repository.android.SynchronizationStateRepositoryImpl
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.FlipperFavoritesRepositoryImpl
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.FlipperHashRepositoryImpl
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.TimestampSynchronizationCheckerImpl
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.DiffMergeHelperImpl
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestRepositoryImpl
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestStorageImpl
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestTimestampRepositoryImpl

class TaskSynchronizationComponentImpl(
    deps: TaskSynchronizationComponentDependencies,
    requestApi: FlipperRequestApi,
    flipperVersionApi: FlipperVersionApi
) : TaskSynchronizationComponent,
    TaskSynchronizationComponentDependencies by deps {
    private val manifestStorage = ManifestStorageImpl(context)

    override val manifestRepository = ManifestRepositoryImpl(
        manifestStorage = manifestStorage
    )

    private val androidHashRepositoryImpl = AndroidHashRepositoryImpl()

    private val flipperHashRepository = FlipperHashRepositoryImpl(
        flipperStorageApi = flipperStorageApi
    )

    private val synchronizationRepository = SynchronizationStateRepositoryImpl(
        utilsKeyApi = utilsKeyApi
    )

    private val diffMergeHelper = DiffMergeHelperImpl(
        simpleKeyApi = simpleKeyApi,
        utilsKeyApi = utilsKeyApi,
        updateKeyApi = updateKeyApi
    )

    private val androidStorage = AndroidKeyStorage(
        simpleKeyApi = simpleKeyApi,
        deleteKeyApi = deleteKeyApi,
        flipperFileApi = flipperFileApi
    )

    private val flipperStorage = FlipperKeyStorage(
        requestApi = requestApi
    )

    private val diffKeyExecutor = DiffKeyExecutorImpl()

    private val keyDiffApplier = KeyDiffApplierImpl(
        diffMergeHelper = diffMergeHelper,
        diffKeyExecutor = diffKeyExecutor,
        androidStorage = androidStorage,
        flipperStorage = flipperStorage
    )

    private val folderKeySynchronization = FolderKeySynchronizationImpl(
        androidHashRepository = androidHashRepositoryImpl,
        flipperHashRepository = flipperHashRepository,
        manifestRepository = manifestRepository,
        synchronizationRepository = synchronizationRepository,
        simpleKeyApi = simpleKeyApi,
        keyDiffApplier = keyDiffApplier,
    )

    private val timestampSynchronizationChecker = TimestampSynchronizationCheckerImpl(
        requestApi = requestApi,
        flipperVersionApi = flipperVersionApi
    )

    private val manifestTimestampRepository = ManifestTimestampRepositoryImpl(
        manifestStorage = manifestStorage
    )

    private val favoritesRepository = FlipperFavoritesRepositoryImpl()

    override val keysSynchronization: KeysSynchronization = KeysSynchronizationImpl(
        folderKeySynchronization,
        timestampSynchronizationChecker,
        manifestTimestampRepository,
        manifestRepository,
        AndroidHashRepositoryImpl(),
        simpleKeyApi
    )

    override val favoriteSynchronization: FavoriteSynchronization = FavoriteSynchronizationImpl(
        favoriteApi = favoriteApi,
        manifestRepository = manifestRepository,
        flipperStorage = flipperStorage,
        favoritesRepository = favoritesRepository
    )
}
