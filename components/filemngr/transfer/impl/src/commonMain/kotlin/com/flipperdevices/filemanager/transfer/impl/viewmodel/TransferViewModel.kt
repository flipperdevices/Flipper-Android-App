package com.flipperdevices.filemanager.transfer.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.Path
import javax.inject.Inject

class TransferViewModel @Inject constructor(
    private val featureProvider: FFeatureProvider,
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "FilesViewModel"

    private val _state = MutableStateFlow<State>(State.Pending)
    val state = _state.asStateFlow()

    private val featureMutex = Mutex()
    private var featureJob: Job? = null
    private var moveMutex = Mutex()
    private var moveJob: Job? = null

    private suspend fun move(
        uploadApi: FFileUploadApi,
        oldPaths: List<Path>,
        targetDir: Path
    ) {
        oldPaths.map { oldPath ->
            val targetPath = targetDir.resolve(oldPath.name)
            uploadApi.move(
                oldPath = oldPath,
                newPath = targetPath
            ).onFailure {
                error(it) { "#onFinish could not move file $oldPaths -> $targetPath" }
            }
        }.firstOrNull { result -> result.isSuccess }?.onSuccess {
            _state.emit(State.Moved(targetDir))
        }
    }

    fun move(oldPaths: List<Path>, targetDir: Path) {
        viewModelScope.launch {
            featureJob?.cancelAndJoin()
            featureMutex.withLock {
                featureJob = featureProvider.get<FStorageFeatureApi>()
                    .onEach { status ->
                        moveJob?.cancelAndJoin()
                        moveMutex.withLock {
                            when (status) {
                                FFeatureStatus.NotFound -> _state.emit(State.Unsupported)
                                FFeatureStatus.Retrieving -> _state.emit(State.Moving)
                                is FFeatureStatus.Supported -> {
                                    moveJob = viewModelScope.launch {
                                        _state.emit(State.Moving)
                                        move(
                                            uploadApi = status.featureApi.uploadApi(),
                                            oldPaths = oldPaths,
                                            targetDir = targetDir
                                        )
                                    }
                                }

                                FFeatureStatus.Unsupported -> _state.emit(State.Unsupported)
                            }
                            moveJob?.join()
                        }
                    }.launchIn(viewModelScope)
                featureJob?.join()
            }
        }
    }

    sealed interface State {
        data object Unsupported : State
        data object Pending : State
        data object Moving : State
        data class Moved(val targetDir: Path) : State
    }
}
