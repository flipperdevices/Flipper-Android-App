package com.flipperdevices.filemanager.listing.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.connection.feature.storageinfo.api.FStorageInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.storageinfo.model.FlipperStorageInformation
import com.flipperdevices.bridge.connection.feature.storageinfo.model.StorageStats
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class StorageInfoViewModel @Inject constructor(
    private val featureProvider: FFeatureProvider,
) : DecomposeViewModel() {

    private fun Flow<FlipperStorageInformation>.mapToModel(): Flow<State> {
        return this.map {
            when (val status = it.externalStorageStatus) {
                is FlipperInformationStatus.InProgress -> State.Loading
                is FlipperInformationStatus.NotStarted -> State.Error
                is FlipperInformationStatus.Ready -> when (val data = status.data) {
                    null, StorageStats.Error -> State.Error
                    is StorageStats.Loaded -> State.Loaded(data.free, data.total)
                }
            }
        }
    }

    val state = flow {
        featureProvider.get<FStorageInfoFeatureApi>()
            .onEach { status ->
                when (status) {
                    FFeatureStatus.NotFound -> emit(State.Error)
                    FFeatureStatus.Retrieving -> emit(State.Loading)
                    FFeatureStatus.Unsupported -> emit(State.Error)
                    is FFeatureStatus.Supported -> {
                        status.featureApi
                            .getStorageInformationFlow()
                            .mapToModel()
                            .onEach { emit(it) }
                            .collect()
                    }
                }
            }.collect()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, State.Loading)

    sealed interface State {
        data object Error : State
        data object Loading : State
        data class Loaded(
            val free: Long,
            val total: Long
        ) : State {
            val used = total - free
        }
    }
}
