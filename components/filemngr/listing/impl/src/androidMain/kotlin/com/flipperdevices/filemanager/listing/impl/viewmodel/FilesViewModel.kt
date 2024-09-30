package com.flipperdevices.filemanager.listing.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FListingStorageApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.core.ktx.jre.toThrowableFlow
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import okio.Path

class FilesViewModel @AssistedInject constructor(
    private val featureProvider: FFeatureProvider,
    @Assisted private val path: Path
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "FilesViewModel"

    private val mutex = Mutex()

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    private suspend fun listFiles(listingApi: FListingStorageApi) {
        listingApi.lsFlow(path.toString())
            .toThrowableFlow()
            .catch { _state.emit(State.CouldNotListPath) }
            .onEach { files ->
                _state.update { state ->
                    when (state) {
                        is State.Loaded -> {
                            state.copy(state.files.plus(files).toImmutableList())
                        }

                        else -> {
                            State.Loaded(files.toImmutableList())
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    private suspend fun invalidate(
        featureStatus: FFeatureStatus<FStorageFeatureApi>
    ) = withLock(mutex, "invalidate") {
        when (featureStatus) {
            FFeatureStatus.Unsupported,
            FFeatureStatus.NotFound -> {
                _state.emit(State.Unsupported)
            }

            is FFeatureStatus.Supported -> {
                listFiles(featureStatus.featureApi.listingApi())
            }

            FFeatureStatus.Retrieving -> {
                _state.emit(State.Loading)
            }
        }
    }

    init {
        featureProvider
            .get<FStorageFeatureApi>()
            .onEach { featureStatus -> invalidate(featureStatus) }
            .launchIn(viewModelScope)
    }

    sealed interface State {
        data object Loading : State
        data object Unsupported : State
        data object CouldNotListPath : State
        data class Loaded(val files: ImmutableList<ListingItem>) : State
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            path: Path
        ): FilesViewModel
    }
}
