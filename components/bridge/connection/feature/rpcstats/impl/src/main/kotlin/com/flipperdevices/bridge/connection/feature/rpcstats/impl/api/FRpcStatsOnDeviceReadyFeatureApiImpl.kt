package com.flipperdevices.bridge.connection.feature.rpcstats.impl.api

import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.rpcstats.api.FRpcStatsOnDeviceReadyFeatureApi
import com.flipperdevices.bridge.connection.feature.storageinfo.api.FStorageInfoFeatureApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.property.getRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

private const val DEVICE_INFO_FIRMWARE_FORK_KEY = "devinfo.firmware.origin.fork"
private const val DEVICE_INFO_FIRMWARE_ORIGIN_KEY = "devinfo.firmware.origin.git"

class FRpcStatsOnDeviceReadyFeatureApiImpl @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    @Assisted private val storageFeatureApi: FStorageInfoFeatureApi,
    @Assisted private val versionFeatureApi: FVersionFeatureApi
) : FRpcStatsOnDeviceReadyFeatureApi, LogTagProvider {
    override val TAG = "FlipperRpcStatsCollector"

    private val mutex = Mutex()
    private var statJob: Job? = null


    override suspend fun onReady() {
        storageFeatureApi.invalidate(
            scope,
            force = false
        )

        val oldJob = statJob
        statJob = scope.launch {
            oldJob?.cancelAndJoin()
            versionFeatureApi
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