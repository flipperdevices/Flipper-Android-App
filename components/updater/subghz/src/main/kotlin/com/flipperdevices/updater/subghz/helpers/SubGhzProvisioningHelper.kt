package com.flipperdevices.updater.subghz.helpers

import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.RegionSource
import com.flipperdevices.metric.api.events.complex.SubGhzProvisioningEvent
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.RegionKt.band
import com.flipperdevices.protobuf.region
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.subghz.model.FailedUploadSubGhzException
import com.flipperdevices.updater.subghz.model.RegionProvisioning
import com.flipperdevices.updater.subghz.model.RegionProvisioningSource
import com.google.protobuf.ByteString
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import javax.inject.Inject

private const val UNKNOWN_REGION = "WW"

interface SubGhzProvisioningHelper {
    suspend fun provideAndUploadSubGhz(serviceApi: FlipperServiceApi)
    suspend fun getRegion(): String?
}

@ContributesBinding(AppGraph::class, SubGhzProvisioningHelper::class)
class SubGhzProvisioningHelperImpl @Inject constructor(
    private val downloaderApi: DownloaderApi,
    private val regionProvisioningHelper: RegionProvisioningHelper,
    private val metricApi: MetricApi,
    private val skipProvisioningHelper: SkipProvisioningHelper
) : SubGhzProvisioningHelper, LogTagProvider {
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
        if (skipProvisioningHelper.shouldSkipProvisioning(serviceApi)) {
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
        } else {
            response.defaults
        }

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
        reportMetric(providedRegions, providedRegion, source ?: RegionProvisioningSource.DEFAULT)
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
}
