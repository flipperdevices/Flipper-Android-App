package com.flipperdevices.filemanager.editor.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.serialspeed.api.FSpeedFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.filemanager.util.constant.FileManagerConstants
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import okio.Path

class DownloadViewModel @AssistedInject constructor(
    @Assisted("fullPathOnFlipper") private val fullPathOnFlipper: Path,
    @Assisted("fullPathOnDevice") private val fullPathOnDevice: Path,
    private val featureProvider: FFeatureProvider,
    private val storageProvider: FlipperStorageProvider
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "DownloadViewModel"
    private val _state = MutableStateFlow<State>(
        State.Downloading(
            downloaded = 0,
            total = 0,
            fullPathOnFlipper = fullPathOnFlipper
        )
    )
    val state = _state.asStateFlow()

    val speedState = featureProvider.get<FSpeedFeatureApi>()
        .stateIn(viewModelScope, SharingStarted.Eagerly, FFeatureStatus.Retrieving)
        .flatMapLatest {
            (it as? FFeatureStatus.Supported<FSpeedFeatureApi>)
                ?.featureApi
                ?.getSpeed()
                ?.map { speedState -> speedState.receiveBytesInSec }
                ?: flowOf(0L)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, 0L)

    private fun startFeature() {
        featureProvider.get<FStorageFeatureApi>()
            .onEach { status ->
                when (status) {
                    FFeatureStatus.NotFound -> _state.emit(State.Unsupported)
                    FFeatureStatus.Retrieving -> _state.emit(
                        State.Downloading(
                            downloaded = 0,
                            total = 0,
                            fullPathOnFlipper = fullPathOnFlipper
                        )
                    )

                    is FFeatureStatus.Supported -> {
                        status.featureApi.downloadApi().download(
                            pathOnFlipper = fullPathOnFlipper.toString(),
                            fileOnAndroid = fullPathOnDevice,
                            progressListener = { current, max ->
                                _state.emit(
                                    State.Downloading(
                                        downloaded = current,
                                        total = max,
                                        fullPathOnFlipper = fullPathOnFlipper
                                    )
                                )
                            }
                        ).onFailure { throwable ->
                            error(throwable) { "Failed to download file $fullPathOnFlipper" }
                            _state.emit(State.CouldNotDownload)
                        }.onSuccess {
                            val metaSize =
                                storageProvider.fileSystem.metadataOrNull(fullPathOnDevice)?.size
                            val isTooLarge = if (metaSize != null) {
                                metaSize > FileManagerConstants.LIMITED_SIZE_BYTES
                            } else {
                                false
                            }
                            if (isTooLarge) {
                                _state.emit(State.TooLarge)
                            } else {
                                _state.emit(State.Downloaded)
                            }
                        }
                    }

                    FFeatureStatus.Unsupported -> _state.emit(State.Unsupported)
                }
            }
            .launchIn(viewModelScope)
    }

    init {
        startFeature()
    }

    sealed interface State {
        data object Unsupported : State
        data object CouldNotDownload : State
        data class Downloading(
            val downloaded: Long,
            val total: Long,
            val fullPathOnFlipper: Path
        ) : State {
            val progress: Float = when (total) {
                0L -> 0f
                else -> (downloaded / total.toFloat()).coerceIn(0f, 1f)
            }
        }

        data object TooLarge : State

        data object Downloaded : State
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            @Assisted("fullPathOnFlipper") fullPathOnFlipper: Path,
            @Assisted("fullPathOnDevice") fullPathOnDevice: Path,
        ): DownloadViewModel
    }
}
