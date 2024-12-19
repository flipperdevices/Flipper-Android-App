package com.flipperdevices.faphub.installation.manifest.impl.utils

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.bridge.connection.feature.storageinfo.api.FStorageInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.storageinfo.model.FlipperStorageInformation
import com.flipperdevices.bridge.connection.feature.storageinfo.model.StorageStats
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.faphub.errors.api.throwable.FlipperNotConnected
import com.flipperdevices.faphub.installation.manifest.error.NoSdCardException
import com.flipperdevices.faphub.installation.manifest.impl.model.FapManifestLoaderState
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestConstants.FAP_MANIFESTS_FOLDER_ON_FLIPPER
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import okio.buffer
import java.io.File

@Suppress("LongParameterList")
class FapManifestsLoader @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    private val parser: FapManifestParser,
    private val dataStoreSettings: DataStore<Settings>,
    private val cacheLoader: FapManifestCacheLoader,
    private val fapExistChecker: FapExistChecker,
    private val fFeatureProvider: FFeatureProvider,
    private val fDeviceOrchestrator: FDeviceOrchestrator
) : LogTagProvider {
    override val TAG = "FapManifestsLoader"
    private val manifestLoaderState = MutableStateFlow<FapManifestLoaderState>(
        FapManifestLoaderState.Loaded(
            items = persistentListOf(),
            isLoading = true
        )
    )

    private var job: Job? = null
    private val jobInvalidateMutex = Mutex()

    fun invalidate() = launchWithLock(jobInvalidateMutex, scope) {
        val oldJob = job
        job = scope.launch {
            oldJob?.cancelAndJoin()
            fapExistChecker.invalidate()
            manifestLoaderState.emit(
                FapManifestLoaderState.Loaded(
                    items = persistentListOf(),
                    isLoading = true
                )
            )
            val fStorageInfoApi = fFeatureProvider.getSync<FStorageInfoFeatureApi>()
            if (fStorageInfoApi == null) {
                error { "#invalidate could not find FStorageInfoFeatureApi" }
                return@launch
            }
            fStorageInfoApi.invalidate(scope = scope)

            combine(
                fDeviceOrchestrator.getState(),
                fStorageInfoApi.getStorageInformationFlow()
            ) { connectionState, flipperStorageInformation ->
                connectionState to flipperStorageInformation
            }.collectLatest { (connectionState, flipperStorageInformation) ->
                runCatching {
                    loadInternal(
                        connectionState = connectionState,
                        storageInformation = flipperStorageInformation
                    )
                }.onFailure {
                    if (it is CancellationException) {
                        throw it
                    } else {
                        manifestLoaderState.emit(FapManifestLoaderState.Failed(it))
                    }
                }
            }
        }
    }

    fun getManifestLoaderState() = manifestLoaderState.asStateFlow()

    @Suppress("LongMethod")
    private suspend fun loadInternal(
        connectionState: FDeviceConnectStatus,
        storageInformation: FlipperStorageInformation
    ) {
        val isUseDevCatalog = dataStoreSettings.data.first().use_dev_catalog

        if (connectionState !is FDeviceConnectStatus.Connected) {
            throw FlipperNotConnected()
        }
        val externalStorageStatus = storageInformation.externalStorageStatus
            as? FlipperInformationStatus.Ready<StorageStats?>
        if (externalStorageStatus == null || externalStorageStatus.data !is StorageStats.Loaded) {
            throw NoSdCardException()
        }
        manifestLoaderState.emit(
            FapManifestLoaderState.Loaded(
                items = persistentListOf(),
                isLoading = true
            )
        )
        info { "Start load manifests" }
        val cacheResult = cacheLoader.loadCache()
        info { "Cache load result is toLoad: ${cacheResult.toLoadNames}, cached: ${cacheResult.cachedNames}" }
        val fapItemsList = mutableListOf<FapManifestItem>()
        cacheResult.cachedNames.mapNotNull { (file, name) ->
            parser.parse(file.readBytes(), name)
        }.filter { fapExistChecker.checkExist(it.path) }
            .filter { it.isDevCatalog == isUseDevCatalog }
            .forEach {
                fapItemsList.add(it)
                manifestLoaderState.emit(
                    FapManifestLoaderState.Loaded(
                        items = fapItemsList.toPersistentList(),
                        isLoading = true
                    )
                )
            }
        info { "Parsed ${fapItemsList.size} manifests from cache" }
        cacheResult.toLoadNames.mapNotNull { name ->
            loadManifestFile(filePath = File(FAP_MANIFESTS_FOLDER_ON_FLIPPER, name).absolutePath)
                ?.let { byteArray -> parser.parse(byteArray, name) }
        }.filter { fapExistChecker.checkExist(it.path) }
            .filter { it.isDevCatalog == isUseDevCatalog }
            .forEach { content ->
                fapItemsList.add(content)
                manifestLoaderState.emit(
                    FapManifestLoaderState.Loaded(
                        items = fapItemsList.toPersistentList(),
                        isLoading = true
                    )
                )
            }
        info { "Parsed ${fapItemsList.size} manifests from flipper" }

        cacheLoader.invalidate(fapItemsList)

        manifestLoaderState.emit(
            FapManifestLoaderState.Loaded(
                items = fapItemsList.toPersistentList(),
                isLoading = false
            )
        )
    }

    private suspend fun loadManifestFile(
        filePath: String
    ): ByteArray? = coroutineScope {
        fFeatureProvider
            .getSync<FStorageFeatureApi>()
            ?.downloadApi()
            ?.source(
                pathOnFlipper = filePath,
                priority = StorageRequestPriority.BACKGROUND,
                scope = this
            )
            ?.buffer()
            ?.readByteArray()
    }

    @AssistedFactory
    interface Factory {
        operator fun invoke(scope: CoroutineScope): FapManifestsLoader
    }
}
