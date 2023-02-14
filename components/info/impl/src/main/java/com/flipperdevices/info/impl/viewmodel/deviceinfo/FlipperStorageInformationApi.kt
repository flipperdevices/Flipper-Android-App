package com.flipperdevices.info.impl.viewmodel.deviceinfo

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.StorageStats
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.FlipperRPCInfoEvent
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.infoRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext


interface FlipperStorageInformationApi {
    fun getStorageInformationFlow(): StateFlow<FlipperStorageInformation>
    suspend fun invalidate(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi,
        force: Boolean = false
    )
}

private const val FLIPPER_PATH_INTERNAL_STORAGE = "/int/"
private const val FLIPPER_PATH_EXTERNAL_STORAGE = "/ext/"

class FlipperStorageInformationApiImpl(
    private val metricApi: MetricApi
) : FlipperStorageInformationApi, LogTagProvider {
    override val TAG = "FlipperStorageInformationApi"

    private val mutex = Mutex()
    private var alreadyRequested = false
    private var job: Job? = null
    private val requestStatusFlow = MutableStateFlow(FlipperStorageInformation())

    override fun getStorageInformationFlow() = requestStatusFlow.asStateFlow()

    override suspend fun invalidate(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi,
        force: Boolean
    ) = withLock(mutex, "invalidate") {
        if (force.not() && alreadyRequested) {
            return@withLock
        }
        alreadyRequested = true

        job?.cancelAndJoin()
        job = scope.launch(Dispatchers.Default) {
            invalidateInternal(requestApi)
        }
    }

    private suspend fun invalidateInternal(
        requestApi: FlipperRequestApi
    ) {
        requestStatusFlow.emit(
            FlipperStorageInformation(
                internalStorageStats = FlipperInformationStatus.InProgress(null),
                externalStorageStats = FlipperInformationStatus.InProgress(null),
            )
        )

        receiveStorageInfo(requestApi, FLIPPER_PATH_INTERNAL_STORAGE) { storageStats ->
            info { "Received internal storage info: $storageStats" }
            val information = requestStatusFlow.updateAndGet {
                it.copy(
                    internalStorageStats = FlipperInformationStatus.Ready(storageStats)
                )
            }
            reportMetric(information)
        }

        requestStatusFlow.update {
            if (it.internalStorageStats !is FlipperInformationStatus.Ready) {
                it.copy(internalStorageStats = FlipperInformationStatus.Ready(null))
            } else {
                it
            }
        }

        receiveStorageInfo(requestApi, FLIPPER_PATH_EXTERNAL_STORAGE) { storageStats ->
            info { "Received external storage info: $storageStats" }
            val information = requestStatusFlow.updateAndGet {
                it.copy(
                    externalStorageStats = FlipperInformationStatus.Ready(storageStats)
                )
            }
            reportMetric(information)
        }

        requestStatusFlow.update {
            if (it.externalStorageStats !is FlipperInformationStatus.Ready) {
                it.copy(externalStorageStats = FlipperInformationStatus.Ready(null))
            } else {
                it
            }
        }
    }


    private suspend fun receiveStorageInfo(
        requestApi: FlipperRequestApi,
        storagePath: String,
        spaceInfoReceiver: suspend (StorageStats) -> Unit
    ) = withContext(Dispatchers.Default) {
        requestApi.request(
            main {
                storageInfoRequest = infoRequest {
                    path = storagePath
                }
            }.wrapToRequest(FlipperRequestPriority.DEFAULT)
        ).collect { response ->
            if (response.commandStatus != Flipper.CommandStatus.OK) {
                spaceInfoReceiver(StorageStats.Error)
                return@collect
            }
            if (!response.hasStorageInfoResponse()) {
                return@collect
            }
            spaceInfoReceiver(
                StorageStats.Loaded(
                    total = response.storageInfoResponse.totalSpace,
                    free = response.storageInfoResponse.freeSpace
                )
            )
        }
    }


    private fun reportMetric(information: FlipperStorageInformation) {
        val externalStatsStatus = information.externalStorageStats
                as? FlipperInformationStatus.Ready
        val internalStatsStatus = information.internalStorageStats
                as? FlipperInformationStatus.Ready
        val externalStats = externalStatsStatus?.data as? StorageStats.Loaded
        val internalStats = internalStatsStatus?.data as? StorageStats.Loaded

        metricApi.reportComplexEvent(
            FlipperRPCInfoEvent(
                sdCardIsAvailable = externalStats != null,
                internalFreeBytes = internalStats?.free ?: 0,
                internalTotalBytes = internalStats?.total ?: 0,
                externalFreeBytes = externalStats?.free ?: 0,
                externalTotalBytes = externalStats?.total ?: 0
            )
        )
    }
}