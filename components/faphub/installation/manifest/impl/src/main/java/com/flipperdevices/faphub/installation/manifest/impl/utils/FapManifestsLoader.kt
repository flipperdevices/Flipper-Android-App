package com.flipperdevices.faphub.installation.manifest.impl.utils

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.rpc.api.model.exceptions.NoSdCardException
import com.flipperdevices.bridge.rpcinfo.api.FlipperStorageInformationApi
import com.flipperdevices.bridge.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.rpcinfo.model.FlipperStorageInformation
import com.flipperdevices.bridge.rpcinfo.model.StorageStats
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.flatten
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.faphub.errors.api.throwable.FlipperNotConnected
import com.flipperdevices.faphub.installation.manifest.impl.model.FapManifestLoaderState
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestConstants.FAP_MANIFESTS_FOLDER_ON_FLIPPER
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.readRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.io.File

@Suppress("LongParameterList")
class FapManifestsLoader @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    private val flipperServiceProvider: FlipperServiceProvider,
    private val parser: FapManifestParser,
    private val dataStoreSettings: DataStore<Settings>,
    private val cacheLoader: FapManifestCacheLoader,
    private val flipperStorageInformationApi: FlipperStorageInformationApi,
    private val fapExistChecker: FapExistChecker
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
            val serviceApi = flipperServiceProvider.getServiceApi()
            flipperStorageInformationApi.invalidate(
                scope = scope,
                serviceApi = serviceApi
            )
            combine(
                serviceApi.connectionInformationApi
                    .getConnectionStateFlow(),
                flipperStorageInformationApi
                    .getStorageInformationFlow()
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
        connectionState: ConnectionState,
        storageInformation: FlipperStorageInformation
    ) {
        val isUseDevCatalog = dataStoreSettings.data.first().useDevCatalog
        val serviceApi = flipperServiceProvider.getServiceApi()
        if (!connectionState.isReady) {
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
        cacheResult.cachedNames.map { (file, name) ->
            parser.parse(file.readBytes(), name)
        }.filterNotNull()
            .filter { fapExistChecker.checkExist(it.path) }
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
        cacheResult.toLoadNames
            .map { name ->
                loadManifestFile(
                    requestApi = serviceApi.requestApi,
                    filePath = File(FAP_MANIFESTS_FOLDER_ON_FLIPPER, name).absolutePath
                )?.let { parser.parse(it, name) }
            }
            .filterNotNull()
            .filter { fapExistChecker.checkExist(it.path) }
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

        manifestLoaderState.emit(
            FapManifestLoaderState.Loaded(
                items = fapItemsList.toPersistentList(),
                isLoading = false
            )
        )
    }

    private suspend fun loadManifestFile(
        requestApi: FlipperRequestApi,
        filePath: String
    ): ByteArray? {
        val responseBytes = requestApi.request(
            main {
                storageReadRequest = readRequest {
                    path = filePath
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).toList().map { response ->
            if (response.hasStorageReadResponse()) {
                response.storageReadResponse.file.data.toByteArray()
            } else {
                return null
            }
        }.flatten()

        return responseBytes
    }

    @AssistedFactory
    interface Factory {
        operator fun invoke(scope: CoroutineScope): FapManifestsLoader
    }
}
