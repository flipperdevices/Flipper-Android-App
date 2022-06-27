package com.flipperdevices.bridge.impl.manager.service.requestservice

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.service.FlipperRpcInformationApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.FlipperRequestRpcInformationStatus
import com.flipperdevices.bridge.api.model.FlipperRpcInformation
import com.flipperdevices.bridge.api.model.StorageStats
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.impl.di.BridgeImplComponent
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.forEachIterable
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.FlipperRPCInfoEvent
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.infoRequest
import com.flipperdevices.protobuf.system.deviceInfoRequest
import com.flipperdevices.shake2report.api.Shake2ReportApi
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

private const val FLIPPER_PATH_INTERNAL_STORAGE = "/int/"
private const val FLIPPER_PATH_EXTERNAL_STORAGE = "/ext/"

class FlipperRpcInformationApiImpl(
    private val scope: CoroutineScope,
    private val metricApi: MetricApi
) : FlipperRpcInformationApi, LogTagProvider {
    override val TAG = "FlipperRpcInformationApi"

    private val mutex = Mutex()
    private val rpcInformationFlow = MutableStateFlow(
        FlipperRpcInformation()
    )
    private val requestStatusFlow = MutableStateFlow<FlipperRequestRpcInformationStatus>(
        FlipperRequestRpcInformationStatus.NotStarted
    )
    private val requestJobs = mutableListOf<Job>()

    @Inject
    lateinit var shake2ReportApi: Shake2ReportApi

    init {
        ComponentHolder.component<BridgeImplComponent>().inject(this)
        rpcInformationFlow.onEach {
            shake2ReportApi.updateRpcInformation(it)
        }.launchIn(scope + Dispatchers.Default)
    }

    override fun getRequestRpcInformationStatus() = requestStatusFlow
    override fun getRpcInformationFlow() = rpcInformationFlow

    suspend fun initialize(requestApi: FlipperRequestApi) = withLock(mutex, "initialize") {
        requestStatusFlow.emit(FlipperRequestRpcInformationStatus.InProgress())
        requestJobs += scope.launch(Dispatchers.Default) {
            receiveStorageInfo(requestApi, FLIPPER_PATH_EXTERNAL_STORAGE) { storageStats ->
                info { "Received external storage info: $storageStats" }
                val information = rpcInformationFlow.updateAndGet {
                    it.copy(
                        externalStorageStats = storageStats
                    )
                }
                reportMetric(information)
            }
            requestStatusFlow.update {
                if (it is FlipperRequestRpcInformationStatus.InProgress) {
                    it.copy(externalStorageRequestFinished = true)
                } else it
            }
        }
        requestJobs += scope.launch(Dispatchers.Default) {
            receiveStorageInfo(requestApi, FLIPPER_PATH_INTERNAL_STORAGE) { storageStats ->
                info { "Received internal storage info: $storageStats" }
                val information = rpcInformationFlow.updateAndGet {
                    it.copy(
                        internalStorageStats = storageStats
                    )
                }
                reportMetric(information)
            }

            requestStatusFlow.updateAndGet {
                if (it is FlipperRequestRpcInformationStatus.InProgress) {
                    it.copy(internalStorageRequestFinished = true)
                } else it
            }
        }
        requestJobs += scope.launch(Dispatchers.Default) {
            requestApi.request(
                main {
                    systemDeviceInfoRequest = deviceInfoRequest { }
                }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
            ).collect { response ->
                if (!response.hasSystemDeviceInfoResponse()) {
                    return@collect
                }
                onApplyInfo(
                    response.systemDeviceInfoResponse.key,
                    response.systemDeviceInfoResponse.value
                )
            }
            requestStatusFlow.update {
                if (it is FlipperRequestRpcInformationStatus.InProgress) {
                    it.copy(rpcDeviceInfoRequestFinished = true)
                } else it
            }
        }
    }

    override suspend fun invalidate(requestApi: FlipperRequestApi) {
        reset()
        initialize(requestApi)
    }

    suspend fun reset() = withLock(mutex, "reset") {
        requestJobs.forEachIterable {
            it.cancelAndJoin()
        }
        requestStatusFlow.emit(FlipperRequestRpcInformationStatus.NotStarted)
        rpcInformationFlow.emit(FlipperRpcInformation())
    }

    private fun reportMetric(information: FlipperRpcInformation) {
        val externalStats = information.externalStorageStats as? StorageStats.Loaded
        val internalStats = information.internalStorageStats as? StorageStats.Loaded

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

    private suspend fun receiveStorageInfo(
        requestApi: FlipperRequestApi,
        storagePath: String,
        spaceInfoReceiver: (StorageStats) -> Unit
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

    private fun onApplyInfo(key: String, value: String) {
        verbose { "Receive: $key=$value" }
        rpcInformationFlow.update {
            it.copy(otherFields = it.otherFields.plus(key to value))
        }
    }
}
