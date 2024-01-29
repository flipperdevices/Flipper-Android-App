package com.flipperdevices.bridge.rpcinfo.impl.stats

import com.flipperdevices.bridge.api.manager.FlipperReadyListener
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants.API_SUPPORTED_GET_REQUEST
import com.flipperdevices.bridge.rpcinfo.api.FlipperStorageInformationApi
import com.flipperdevices.bridge.rpcinfo.impl.shaketoreport.FlipperInformationMapping
import com.flipperdevices.bridge.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.rpcinfo.model.FlipperStorageInformation
import com.flipperdevices.bridge.rpcinfo.model.StorageStats
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.FlipperRPCInfoEvent
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.property.getRequest
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

private const val DEVICE_INFO_FIRMWARE_FORK_KEY = "devinfo.firmware.origin.fork"
private const val DEVICE_INFO_FIRMWARE_ORIGIN_KEY = "devinfo.firmware.origin.git"

@ContributesMultibinding(AppGraph::class, FlipperReadyListener::class)
class FlipperRpcStatsCollector @Inject constructor(
    private val storageInformationApi: FlipperStorageInformationApi,
    private val flipperServiceApiProvider: FlipperServiceProvider,
    private val metricApi: MetricApi,
    private val shake2ReportApi: Shake2ReportApi
) : FlipperReadyListener, LogTagProvider {
    override val TAG = "FlipperRpcStatsCollector"

    private val mutex = Mutex()
    private var statJob: Job? = null

    override suspend fun onFlipperReady(scope: CoroutineScope) {
        val serviceApi = flipperServiceApiProvider.getServiceApi()

        storageInformationApi.invalidate(
            scope,
            serviceApi,
            force = false
        )

        val oldJob = statJob
        statJob = scope.launch {
            oldJob?.cancelAndJoin()
            serviceApi
                .flipperVersionApi
                .getVersionInformationFlow()
                .collect { version ->
                    if (version != null && version > API_SUPPORTED_GET_REQUEST) {
                        startRequestApiInformation(serviceApi.requestApi)
                    }
                }
        }
    }

    private suspend fun startRequestApiInformation(requestApi: FlipperRequestApi) {
        val forkResponse = requestApi.request(
            flowOf(
                main {
                    propertyGetRequest = getRequest { key = DEVICE_INFO_FIRMWARE_FORK_KEY }
                }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
            )
        )
        val forkValue = if (forkResponse.commandStatus == Flipper.CommandStatus.OK) {
            forkResponse.propertyGetResponse.value
        } else {
            null
        }
        val originResponse = requestApi.request(
            flowOf(
                main {
                    propertyGetRequest = getRequest { key = DEVICE_INFO_FIRMWARE_ORIGIN_KEY }
                }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
            )
        )
        val originValue = if (originResponse.commandStatus == Flipper.CommandStatus.OK) {
            originResponse.propertyGetResponse.value
        } else {
            null
        }

        storageInformationApi.getStorageInformationFlow().collect { storageInformation ->
            if (storageInformation.externalStorageStatus is FlipperInformationStatus.Ready &&
                storageInformation.internalStorageStatus is FlipperInformationStatus.Ready
            ) {
                reportMetric(storageInformation, forkValue, originValue)
            }
        }
    }

    private suspend fun reportMetric(
        information: FlipperStorageInformation,
        firmwareFork: String?,
        firmwareOrigin: String?
    ) = withLock(mutex, "report") {
        val externalStatsStatus =
            information.externalStorageStatus as? FlipperInformationStatus.Ready
        val internalStatsStatus =
            information.internalStorageStatus as? FlipperInformationStatus.Ready
        val externalStats = externalStatsStatus?.data as? StorageStats.Loaded
        val internalStats = internalStatsStatus?.data as? StorageStats.Loaded

        metricApi.reportComplexEvent(
            FlipperRPCInfoEvent(
                sdCardIsAvailable = externalStats != null,
                internalFreeBytes = internalStats?.free ?: 0,
                internalTotalBytes = internalStats?.total ?: 0,
                externalFreeBytes = externalStats?.free ?: 0,
                externalTotalBytes = externalStats?.total ?: 0,
                firmwareForkName = firmwareFork,
                firmwareGitUrl = firmwareOrigin
            )
        )
        shake2ReportApi.setExtra(FlipperInformationMapping.convert(information))
    }
}
