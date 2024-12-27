package com.flipperdevices.updater.subghz.helpers

import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.copyWithProgress
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.RegionSource
import com.flipperdevices.metric.api.events.complex.SubGhzProvisioningEvent
import com.flipperdevices.protobuf.Region
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.subghz.model.FailedUploadSubGhzException
import com.flipperdevices.updater.subghz.model.RegionProvisioning
import com.flipperdevices.updater.subghz.model.RegionProvisioningSource
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okio.ByteString.Companion.encode
import okio.buffer
import okio.source
import java.nio.charset.Charset
import javax.inject.Inject

private const val UNKNOWN_REGION = "WW"
const val REGION_FILE = "/int/.region_data"

interface SubGhzProvisioningHelper {
    suspend fun provideAndUploadSubGhz(
        fGetInfoFeatureApi: FGetInfoFeatureApi,
        fFileUploadApi: FFileUploadApi
    )

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
        fGetInfoFeatureApi: FGetInfoFeatureApi,
        fFileUploadApi: FFileUploadApi
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        if (skipProvisioningHelper.shouldSkipProvisioning(fGetInfoFeatureApi)) {
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

        val regionData = Region(
            country_code = finalCodeRegion.encode(Charset.forName("ASCII")),
            bands = providedBands.map {
                Region.Band(
                    start = it.start.toInt(),
                    end = it.end.toInt(),
                    power_limit = it.maxPower,
                    duty_cycle = it.dutyCycle.toInt()
                )
            }
        ).encode()
        try {
            regionData.inputStream().source().buffer().use { bufferedSource ->
                fFileUploadApi.sink(REGION_FILE).use { sink ->
                    bufferedSource.copyWithProgress(
                        sink = sink,
                        progressListener = { _, _ -> },
                        sourceLength = { regionData.size.toLong() }
                    )
                }
            }
        } catch (e: Exception) {
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
