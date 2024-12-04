package com.flipperdevices.filemanager.download.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.serialspeed.api.FSpeedFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.share.PlatformShareHelper
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.filemanager.download.model.DownloadableFile
import flipperapp.components.filemngr.download.impl.generated.resources.Res
import flipperapp.components.filemngr.download.impl.generated.resources.fm_share_title
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okio.Path
import org.jetbrains.compose.resources.getString
import javax.inject.Inject

class DownloadViewModel @Inject constructor(
    private val featureProvider: FFeatureProvider,
    private val platformShareHelper: PlatformShareHelper
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "DownloadViewModel"

    private val _state = MutableStateFlow<State>(State.Pending)
    val state = _state.asStateFlow()

    private val mutex = Mutex()

    private var _featureJob: Job? = null

    private suspend fun download(storageFeatureApi: FStorageFeatureApi, flipperFileFullPath: Path) {
        val pathOnAndroid = platformShareHelper.provideSharableFile(flipperFileFullPath.name)
        storageFeatureApi.downloadApi().download(
            pathOnFlipper = flipperFileFullPath.toString(),
            fileOnAndroid = pathOnAndroid.path,
            progressListener = { current, max ->
                _state.update { state ->
                    (state as? State.Downloading)
                        ?.copy(downloadedSize = current, totalSize = max)
                        ?: State.Downloading(
                            downloadedSize = current,
                            totalSize = max,
                            downloadSpeed = 0L,
                            fullPath = flipperFileFullPath
                        )
                }
            }
        ).onFailure { exception ->
            error(exception) { "Can't download $flipperFileFullPath" }
            _state.emit(State.Error)
        }.onSuccess {
            withContext(Dispatchers.Main) {
                platformShareHelper.shareFile(
                    file = pathOnAndroid,
                    title = getString(Res.string.fm_share_title)
                )
            }
            _state.emit(State.Pending)
            viewModelScope.launch { _featureJob?.cancelAndJoin() }
        }
    }

    fun onCancel() {
        viewModelScope.launch {
            println("DownloadViewModel cancelling")
            _featureJob?.cancelAndJoin()
            println("DownloadViewModel cancelled")
            _state.emit(State.Pending)
        }
    }

    fun tryDownload(file: DownloadableFile) {
        viewModelScope.launch {
            _featureJob?.cancelAndJoin()

            mutex.withLock {
                _featureJob = featureProvider.get<FStorageFeatureApi>()
                    .onEach { storageFeatureStatus ->
                        when (storageFeatureStatus) {
                            FFeatureStatus.NotFound,
                            FFeatureStatus.Unsupported -> _state.emit(State.NotSupported)

                            FFeatureStatus.Retrieving -> {
                                _state.emit(
                                    State.Downloading(
                                        downloadedSize = 0L,
                                        totalSize = file.size,
                                        downloadSpeed = 0L,
                                        fullPath = file.fullPath
                                    )
                                )
                            }

                            is FFeatureStatus.Supported -> {
                                _state.emit(
                                    State.Downloading(
                                        downloadedSize = 0L,
                                        totalSize = file.size,
                                        downloadSpeed = 0L,
                                        fullPath = file.fullPath
                                    )
                                )
                                download(
                                    storageFeatureApi = storageFeatureStatus.featureApi,
                                    flipperFileFullPath = file.fullPath
                                )
                            }
                        }
                    }.catch { it.printStackTrace() }.launchIn(viewModelScope)
                _featureJob?.join()
            }
        }
    }

    private fun collectSpeedState() {
        featureProvider.get<FSpeedFeatureApi>()
            .flatMapLatest { storageFeatureStatus ->
                when (storageFeatureStatus) {
                    is FFeatureStatus.Supported ->
                        storageFeatureStatus.featureApi
                            .getSpeed()
                            .map { fSerialSpeed -> fSerialSpeed.receiveBytesInSec }

                    FFeatureStatus.Retrieving,
                    FFeatureStatus.NotFound,
                    FFeatureStatus.Unsupported -> flowOf(0L)
                }
            }
            .filter { _featureJob?.isActive == true }
            .onEach { speed ->
                _state.update { state ->
                    (state as? State.Downloading)
                        ?.copy(downloadSpeed = speed)
                        ?: state
                }
            }
            .launchIn(viewModelScope)
    }

    init {
        collectSpeedState()
    }

    sealed interface State {
        data object Pending : State
        data object NotSupported : State
        data object Error : State
        data class Downloading(
            val downloadedSize: Long,
            val fullPath: Path,
            val totalSize: Long,
            val downloadSpeed: Long
        ) : State
    }
}
