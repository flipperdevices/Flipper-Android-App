package com.flipperdevices.filemanager.listing.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.connection.feature.storageinfo.api.FStorageInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.storageinfo.model.StorageStats
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class StorageInfoViewModel @Inject constructor(
    private val featureProvider: FFeatureProvider,
) : DecomposeViewModel() {
    val state = flow {
        featureProvider.get<FStorageInfoFeatureApi>()
            .onEach { status ->
                when (status) {
                    FFeatureStatus.NotFound -> emit(Model.Error)
                    FFeatureStatus.Retrieving -> emit(Model.Loading)
                    FFeatureStatus.Unsupported -> emit(Model.Error)
                    is FFeatureStatus.Supported -> {
                        status.featureApi.getStorageInformationFlow()
                            .onEach { storageInformation ->
                                when (
                                    val externalStatus =
                                        storageInformation.externalStorageStatus
                                ) {
                                    is FlipperInformationStatus.InProgress -> {
                                        emit(Model.Loading)
                                    }

                                    is FlipperInformationStatus.NotStarted -> {
                                        emit(Model.Error)
                                    }

                                    is FlipperInformationStatus.Ready -> {
                                        when (val data = externalStatus.data) {
                                            is StorageStats.Loaded -> {
                                                emit(Model.Loaded(data.free, data.total))
                                            }

                                            StorageStats.Error,
                                            null -> emit(Model.Error)
                                        }
                                    }
                                }
                            }.collect()
                    }
                }
            }.collect()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Model.Loading)

    sealed interface Model {
        data object Error : Model
        data object Loading : Model
        data class Loaded(val free: Long, val total: Long) : Model {
            val used = total - free
        }
    }
}
