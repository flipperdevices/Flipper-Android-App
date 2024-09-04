package com.flipperdevices.bridge.connection.feature.rpcstats.impl.api

import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiProperty
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.connection.feature.rpcstats.api.FRpcStatsOnDeviceReadyFeatureApi
import com.flipperdevices.bridge.connection.feature.rpcstats.impl.shaketoreport.FlipperInformationMapping
import com.flipperdevices.bridge.connection.feature.storageinfo.api.FStorageInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.storageinfo.model.FlipperStorageInformation
import com.flipperdevices.bridge.connection.feature.storageinfo.model.StorageStats
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.FlipperRPCInfoEvent
import com.flipperdevices.shake2report.api.Shake2ReportApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class FRpcStatsOnDeviceReadyFeatureApiImpl @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    @Assisted private val storageFeatureApi: FStorageInfoFeatureApi,
    @Assisted private val getInfoFeatureApiNullable: FGetInfoFeatureApi?,
    private val metricApi: MetricApi,
    private val shake2ReportApi: Shake2ReportApi
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
            if (getInfoFeatureApiNullable != null) {
                startRequestApiInformation(getInfoFeatureApiNullable)
            }
        }
    }

    private suspend fun startRequestApiInformation(getInfoFeatureApi: FGetInfoFeatureApi) {
        val fork = getInfoFeatureApi.get(FGetInfoApiProperty.DeviceInfo.FIRMWARE_FORK).getOrNull()
        val origin = getInfoFeatureApi.get(FGetInfoApiProperty.DeviceInfo.FIRMWARE_ORIGIN).getOrNull()

        storageFeatureApi.getStorageInformationFlow().collect { storageInformation ->
            if (storageInformation.externalStorageStatus is FlipperInformationStatus.Ready &&
                storageInformation.internalStorageStatus is FlipperInformationStatus.Ready
            ) {
                reportMetric(storageInformation, fork, origin)
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

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            scope: CoroutineScope,
            storageFeatureApi: FStorageInfoFeatureApi,
            getInfoFeatureApiNullable: FGetInfoFeatureApi?
        ): FRpcStatsOnDeviceReadyFeatureApiImpl
    }
}
