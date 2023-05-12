package com.flipperdevices.bridge.rpcinfo.impl

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.rpcinfo.api.FlipperStorageInformationApi
import com.flipperdevices.bridge.rpcinfo.impl.shaketoreport.FlipperInformationMapping
import com.flipperdevices.bridge.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.rpcinfo.model.FlipperStorageInformation
import com.flipperdevices.bridge.rpcinfo.model.StorageStats
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.FlipperRPCInfoEvent
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.infoRequest
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

private const val FLIPPER_PATH_INTERNAL_STORAGE = "/int/"
private const val FLIPPER_PATH_EXTERNAL_STORAGE = "/ext/"

@ContributesBinding(AppGraph::class, FlipperStorageInformationApi::class)
class FlipperStorageInformationApiImpl @Inject constructor(
    private val metricApi: MetricApi,
    private val shake2ReportApi: Shake2ReportApi
) : FlipperStorageInformationApi, LogTagProvider {
    override val TAG = "FlipperStorageInformationApi"

    private val mutex = Mutex()
    private var alreadyRequested = false
    private var job: Job? = null
    private val storageInformationFlow = MutableStateFlow(FlipperStorageInformation())

    override fun getStorageInformationFlow() = storageInformationFlow.asStateFlow()
    override suspend fun invalidate(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        force: Boolean
    ) = withLock(mutex, "invalidate") {
        if (force.not() && alreadyRequested) {
            return@withLock
        }
        alreadyRequested = true

        job?.cancelAndJoin()
        job = scope.launch {
            serviceApi.connectionInformationApi
                .getConnectionStateFlow().collect { connectionState ->
                    when (connectionState) {
                        is ConnectionState.Ready -> invalidateInternal(serviceApi.requestApi)
                        else -> {}
                    }
                }
        }
    }

    override suspend fun reset() = withLock(mutex, "reset") {
        alreadyRequested = false
        job?.cancelAndJoin()
        storageInformationFlow.emit(FlipperStorageInformation())
    }

    private suspend fun invalidateInternal(
        requestApi: FlipperRequestApi
    ) {
        storageInformationFlow.emit(
            FlipperStorageInformation(
                internalStorageStatus = FlipperInformationStatus.InProgress(null),
                externalStorageStatus = FlipperInformationStatus.InProgress(null),
            )
        )

        receiveStorageInfo(requestApi, FLIPPER_PATH_EXTERNAL_STORAGE) { storageStats ->
            info { "Received external storage info: $storageStats" }
            val information = storageInformationFlow.updateAndGet {
                it.copy(
                    externalStorageStatus = FlipperInformationStatus.Ready(storageStats)
                )
            }
            reportMetric(information)
        }

        storageInformationFlow.update {
            if (it.externalStorageStatus !is FlipperInformationStatus.Ready) {
                it.copy(externalStorageStatus = FlipperInformationStatus.Ready(null))
            } else {
                it
            }
        }

        receiveStorageInfo(requestApi, FLIPPER_PATH_INTERNAL_STORAGE) { storageStats ->
            info { "Received internal storage info: $storageStats" }
            val information = storageInformationFlow.updateAndGet {
                it.copy(
                    internalStorageStatus = FlipperInformationStatus.Ready(storageStats)
                )
            }
            reportMetric(information)
        }

        storageInformationFlow.update {
            if (it.internalStorageStatus !is FlipperInformationStatus.Ready) {
                it.copy(internalStorageStatus = FlipperInformationStatus.Ready(null))
            } else {
                it
            }
        }
    }

    private suspend fun receiveStorageInfo(
        requestApi: FlipperRequestApi,
        storagePath: String,
        spaceInfoReceiver: suspend (StorageStats) -> Unit
    ) {
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
        val externalStatsStatus = information.externalStorageStatus
            as? FlipperInformationStatus.Ready
        val internalStatsStatus = information.internalStorageStatus
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
        shake2ReportApi.setExtra(FlipperInformationMapping.convert(information))
    }
}
