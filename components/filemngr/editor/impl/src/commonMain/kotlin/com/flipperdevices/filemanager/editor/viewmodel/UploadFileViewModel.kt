package com.flipperdevices.filemanager.editor.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.serialspeed.api.FSpeedFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
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
import kotlinx.coroutines.flow.update
import okio.Path

class UploadFileViewModel @AssistedInject constructor(
    @Assisted("fullPathOnFlipper") private val fullPathOnFlipper: Path,
    @Assisted("fullPathOnDevice") private val fullPathOnDevice: Path,
    private val featureProvider: FFeatureProvider,
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "UploadViewModel"

    private val _state = MutableStateFlow<State>(
        State.Uploading(
            fullPathOnFlipper = fullPathOnFlipper,
            uploaded = 0L,
            total = 0L
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
                    FFeatureStatus.Retrieving -> {
                        _state.emit(
                            State.Uploading(
                                fullPathOnFlipper = fullPathOnFlipper,
                                uploaded = 0L,
                                total = 0L
                            )
                        )
                    }

                    is FFeatureStatus.Supported -> {
                        status.featureApi.uploadApi()
                            .upload(
                                pathOnFlipper = fullPathOnFlipper.toString(),
                                fileOnAndroid = fullPathOnDevice,
                                progressListener = { current, max ->
                                    _state.update {
                                        State.Uploading(
                                            fullPathOnFlipper = fullPathOnFlipper,
                                            uploaded = current,
                                            total = max
                                        )
                                    }
                                }
                            ).onFailure {
                                error(it) { "#startFeature could not save file $fullPathOnFlipper" }
                                _state.emit(State.Error)
                            }.onSuccess {
                                _state.update { state ->
                                    (state as? State.Uploading)?.let { uploadingState ->
                                        State.Saved(
                                            fullPathOnFlipper = uploadingState.fullPathOnFlipper,
                                            size = uploadingState.total
                                        )
                                    } ?: state
                                }
                            }
                    }

                    FFeatureStatus.Unsupported -> _state.emit(State.Unsupported)
                }
            }.launchIn(viewModelScope)
    }

    init {
        startFeature()
    }

    sealed interface State {
        data object Unsupported : State
        data object Error : State
        data class Uploading(
            val fullPathOnFlipper: Path,
            val uploaded: Long,
            val total: Long
        ) : State {
            val progress: Float = when (total) {
                0L -> 0f
                else -> (uploaded / total.toFloat()).coerceIn(0f, 1f)
            }
        }

        data class Saved(
            val fullPathOnFlipper: Path,
            val size: Long
        ) : State
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            @Assisted("fullPathOnFlipper") fullPathOnFlipper: Path,
            @Assisted("fullPathOnDevice") fullPathOnDevice: Path,
        ): UploadFileViewModel
    }
}
