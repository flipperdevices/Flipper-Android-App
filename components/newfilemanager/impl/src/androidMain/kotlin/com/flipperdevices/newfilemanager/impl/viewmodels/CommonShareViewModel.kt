package com.flipperdevices.newfilemanager.impl.viewmodels

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.serialspeed.api.FSpeedFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.newfilemanager.impl.model.DownloadProgress
import com.flipperdevices.newfilemanager.impl.model.ShareState
import com.flipperdevices.newfilemanager.impl.model.SpeedState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class CommonShareViewModel(
    private val featureProvider: FFeatureProvider,
    protected val fileName: String,
    private val defaultProgress: DownloadProgress
) : DecomposeViewModel() {
    private val speedState = MutableStateFlow<SpeedState>(SpeedState.Unknown)
    protected val shareStateFlow = MutableStateFlow<ShareState>(
        ShareState.Ready(
            fileName,
            defaultProgress
        )
    )

    fun getSpeedState() = speedState.asStateFlow()
    fun getShareState() = shareStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            featureProvider.get<FStorageFeatureApi>()
                .collectLatest { storageFeatureStatus ->
                    when (storageFeatureStatus) {
                        FFeatureStatus.NotFound,
                        FFeatureStatus.Unsupported -> shareStateFlow.emit(ShareState.Error)

                        FFeatureStatus.Retrieving -> shareStateFlow.emit(
                            ShareState.Ready(
                                fileName,
                                defaultProgress
                            )
                        )

                        is FFeatureStatus.Supported -> start(storageFeatureStatus.featureApi)
                    }
                }
        }
        viewModelScope.launch {
            featureProvider.get<FSpeedFeatureApi>()
                .collectLatest { speedFeatureStatus ->
                    when (speedFeatureStatus) {
                        FFeatureStatus.NotFound,
                        FFeatureStatus.Retrieving,
                        FFeatureStatus.Unsupported -> speedState.emit(SpeedState.Unknown)

                        is FFeatureStatus.Supported ->
                            speedFeatureStatus
                                .featureApi
                                .getSpeed()
                                .collectLatest {
                                    speedState.emit(SpeedState.Ready(it))
                                }
                    }
                }
        }
    }

    protected abstract suspend fun start(storageFeatureApi: FStorageFeatureApi)
}
