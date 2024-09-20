package com.flipperdevices.bridge.connection.feature.storageinfo.impl.api

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.connection.feature.storageinfo.api.FStorageInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.storageinfo.model.FlipperStorageInformation
import com.flipperdevices.bridge.connection.feature.storageinfo.model.StorageStats
import com.flipperdevices.core.ktx.jre.toThrowableFlow
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.InfoRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val FLIPPER_PATH_INTERNAL_STORAGE = "/int/"
private const val FLIPPER_PATH_EXTERNAL_STORAGE = "/ext/"

class FStorageInfoFeatureApiImpl @AssistedInject constructor(
    @Assisted private val rpcFeatureApi: FRpcFeatureApi,
    @Assisted private val scope: CoroutineScope
) : FStorageInfoFeatureApi, LogTagProvider {
    override val TAG = "FlipperStorageInformationApi"

    private val mutex = kotlinx.coroutines.sync.Mutex()
    private var alreadyRequested = false
    private var job: Job? = null
    private val storageInformationFlow = MutableStateFlow(FlipperStorageInformation())

    override fun getStorageInformationFlow() = storageInformationFlow.asStateFlow()

    init {
        scope.launch {
            invalidate(scope)
        }
    }

    override suspend fun invalidate(
        scope: CoroutineScope,
        force: Boolean
    ) = withLock(mutex, "invalidate") {
        if (force.not() && alreadyRequested) {
            return@withLock
        }
        alreadyRequested = true

        job?.cancelAndJoin()
        job = scope.launch {
            invalidateInternal()
        }
    }

    override suspend fun reset() = withLock(mutex, "reset") {
        alreadyRequested = false
        job?.cancelAndJoin()
        storageInformationFlow.emit(FlipperStorageInformation())
    }

    private suspend fun invalidateInternal() {
        storageInformationFlow.emit(
            FlipperStorageInformation(
                internalStorageStatus = FlipperInformationStatus.InProgress(
                    null
                ),
                externalStorageStatus = FlipperInformationStatus.InProgress(
                    null
                ),
            )
        )

        receiveStorageInfo(
            FLIPPER_PATH_EXTERNAL_STORAGE
        ) { storageStats ->
            info { "Received external storage info: $storageStats" }
            storageInformationFlow.update {
                it.copy(
                    externalStorageStatus = FlipperInformationStatus.Ready(
                        storageStats
                    )
                )
            }
        }

        storageInformationFlow.update {
            if (it.externalStorageStatus !is FlipperInformationStatus.Ready) {
                it.copy(
                    externalStorageStatus = FlipperInformationStatus.Ready(
                        null
                    )
                )
            } else {
                it
            }
        }

        receiveStorageInfo(
            FLIPPER_PATH_INTERNAL_STORAGE
        ) { storageStats ->
            info { "Received internal storage info: $storageStats" }
            storageInformationFlow.update {
                it.copy(
                    internalStorageStatus = FlipperInformationStatus.Ready(
                        storageStats
                    )
                )
            }
        }

        storageInformationFlow.update {
            if (it.internalStorageStatus !is FlipperInformationStatus.Ready) {
                it.copy(
                    internalStorageStatus = FlipperInformationStatus.Ready(
                        null
                    )
                )
            } else {
                it
            }
        }
    }

    private suspend fun receiveStorageInfo(
        storagePath: String,
        spaceInfoReceiver: suspend (StorageStats) -> Unit
    ) {
        rpcFeatureApi.request(
            Main(
                storage_info_request = InfoRequest(
                    path = storagePath
                )
            ).wrapToRequest()
        ).toThrowableFlow()
            .catch {
                spaceInfoReceiver(StorageStats.Error)
            }.collect { response ->
                val storageInfoResponse = response.storage_info_response ?: return@collect
                spaceInfoReceiver(
                    StorageStats.Loaded(
                        total = storageInfoResponse.total_space,
                        free = storageInfoResponse.free_space
                    )
                )
            }
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            rpcFeatureApi: FRpcFeatureApi,
            scope: CoroutineScope
        ): FStorageInfoFeatureApiImpl
    }
}
