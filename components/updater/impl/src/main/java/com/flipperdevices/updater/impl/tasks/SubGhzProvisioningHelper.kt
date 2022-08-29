package com.flipperdevices.updater.impl.tasks

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.manager.service.FlipperRpcInformationApi
import com.flipperdevices.bridge.api.model.FlipperRequestRpcInformationStatus
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.RegionSource
import com.flipperdevices.metric.api.events.complex.SubGhzProvisioningEvent
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.RegionKt.band
import com.flipperdevices.protobuf.region
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.api.SubGhzProvisioningHelperApi
import com.flipperdevices.updater.impl.model.FailedUploadSubGhzException
import com.flipperdevices.updater.impl.model.RegionProvisioning
import com.flipperdevices.updater.impl.model.RegionProvisioningSource
import com.google.protobuf.ByteString
import com.squareup.anvil.annotations.ContributesBinding
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

private const val UNKNOWN_REGION = "WW"
private const val RPC_INFORMATION_TIMEOUT_MS = 1_000L * 60 // 10 seconds

@ContributesBinding(AppGraph::class, SubGhzProvisioningHelperApi::class)
class SubGhzProvisioningHelperImpl @Inject constructor(
    private val downloaderApi: DownloaderApi,
    private val regionProvisioningHelper: RegionProvisioningHelper,
    private val metricApi: MetricApi,
    private val settings: DataStore<Settings>
) : SubGhzProvisioningHelperApi, LogTagProvider {
    override val TAG = "SubGhzProvisioningHelper"

    override suspend fun getRegion(): String? {
        val response = downloaderApi.getSubGhzProvisioning()
        val providedRegions = regionProvisioningHelper.provideRegion(
            response.country?.uppercase()
        )
        return providedRegions.provideRegion().first?.uppercase()
    }

    override suspend fun provideAndUploadSubGhz(
        serviceApi: FlipperServiceApi
    ) = withContext(Dispatchers.Default) {
        if (skipProvisioning(serviceApi.flipperRpcInformationApi)) {
            info { "Skip provisioning" }
            return@withContext
        }

        val response = downloaderApi.getSubGhzProvisioning()
        val providedRegions = regionProvisioningHelper.provideRegion(
            response.country?.uppercase()
        )
        val (providedRegion, source) = providedRegions.provideRegion()
        val providedBands = if (providedRegion != null) {
            response.countries[providedRegion.uppercase()] ?: response.defaults
        } else response.defaults

        val finalCodeRegion = providedRegion?.uppercase() ?: UNKNOWN_REGION

        val regionData = region {
            countryCode = ByteString.copyFrom(
                finalCodeRegion,
                Charset.forName("ASCII")
            )
            bands.addAll(
                providedBands.map {
                    band {
                        start = it.start.toInt()
                        end = it.end.toInt()
                        powerLimit = it.maxPower
                        dutyCycle = it.dutyCycle.toInt()
                    }
                }
            )
        }.toByteArray()
        val writeFileResponse = ByteArrayInputStream(regionData).use { inputStream ->
            val flow = streamToCommandFlow(
                inputStream,
                fileSize = regionData.size.toLong()
            ) { chunkData ->
                storageWriteRequest = writeRequest {
                    path = Constants.PATH.REGION_FILE
                    file = file { data = chunkData }
                }
            }.map { it.wrapToRequest() }
            serviceApi.requestApi.request(flow)
        }
        if (writeFileResponse.commandStatus != Flipper.CommandStatus.OK) {
            throw FailedUploadSubGhzException()
        }
        rememberRegion(code = finalCodeRegion)
        reportMetric(providedRegions, providedRegion, source ?: RegionProvisioningSource.DEFAULT)
    }

    private suspend fun skipProvisioning(
        flipperRpcInformationApi: FlipperRpcInformationApi
    ): Boolean {
        val ignoreSubGhzProvisioning = settings.data.first().ignoreSubghzProvisioningOnZeroRegion
        info { "ignoreSubGhzProvisioning disabled, so continue subghz provisioning" }

        if (!ignoreSubGhzProvisioning) {
            return false
        }
        info { "Try receive rpcInformationStatus" }

        withTimeoutOrNull(RPC_INFORMATION_TIMEOUT_MS) {
            flipperRpcInformationApi.getRequestRpcInformationStatus()
                .filter {
                    it is FlipperRequestRpcInformationStatus.InProgress &&
                        it.rpcDeviceInfoRequestFinished
                }.first()
        } ?: return false

        val rpcInformation = flipperRpcInformationApi.getRpcInformationFlow().first()
        val hardwareRegion = rpcInformation.flipperDeviceInfo.hardwareRegion?.toIntOrNull()
            ?: return false
        if (hardwareRegion != 0) {
            info { "Hardware region not zero, so return false" }
            return false
        }
        info { "Region hardware 0 and configuration enabled, skip subghz provisioning" }

        return true
    }

    private fun reportMetric(
        regionProvisioning: RegionProvisioning,
        providedRegion: String?,
        source: RegionProvisioningSource
    ) {
        metricApi.reportComplexEvent(
            SubGhzProvisioningEvent(
                regionNetwork = regionProvisioning.regionFromNetwork,
                regionSimOne = regionProvisioning.regionFromSim,
                regionIp = regionProvisioning.regionFromIp,
                regionSystem = regionProvisioning.regionSystem,
                regionProvided = providedRegion,
                regionSource = when (source) {
                    RegionProvisioningSource.SIM_NETWORK -> RegionSource.SIM_NETWORK
                    RegionProvisioningSource.SIM_COUNTRY -> RegionSource.SIM_COUNTRY
                    RegionProvisioningSource.GEO_IP -> RegionSource.GEO_IP
                    RegionProvisioningSource.SYSTEM -> RegionSource.SYSTEM
                    RegionProvisioningSource.DEFAULT -> RegionSource.DEFAULT
                },
                isRoaming = regionProvisioning.isRoaming
            )
        )
    }

    private suspend fun rememberRegion(code: String) {
        settings.updateData {
            it.toBuilder()
                .setRegion(code)
                .build()
        }
    }
}
