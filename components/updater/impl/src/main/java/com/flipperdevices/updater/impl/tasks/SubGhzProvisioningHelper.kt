package com.flipperdevices.updater.impl.tasks

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.RegionKt.band
import com.flipperdevices.protobuf.region
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.impl.model.FailedUploadSubGhzException
import com.google.protobuf.ByteString
import com.squareup.anvil.annotations.ContributesBinding
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val REGION_FILE_PATH = "/int/.region_data"
private const val UNKNOWN_REGION = "WW"

interface SubGhzProvisioningHelper {
    suspend fun provideAndUploadSubGhz(requestApi: FlipperRequestApi)
}

@ContributesBinding(AppGraph::class, SubGhzProvisioningHelper::class)
class SubGhzProvisioningHelperImpl @Inject constructor(
    private val downloaderApi: DownloaderApi,
    private val regionProvisioningHelper: RegionProvisioningHelper
) : SubGhzProvisioningHelper, LogTagProvider {
    override val TAG = "SubGhzProvisioningHelper"

    override suspend fun provideAndUploadSubGhz(
        requestApi: FlipperRequestApi
    ) = withContext(Dispatchers.Default) {
        val response = downloaderApi.getSubGhzProvisioning()
        val providedRegion = regionProvisioningHelper.provideRegion(response.country)
        val providedBands = if (providedRegion != null) {
            response.countries[providedRegion.region.uppercase()] ?: response.defaults
        } else response.defaults

        val regionData = region {
            countryCode = ByteString.copyFrom(
                providedRegion?.region?.uppercase() ?: UNKNOWN_REGION,
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
                    path = REGION_FILE_PATH
                    file = file { data = chunkData }
                }
            }.map { it.wrapToRequest() }
            requestApi.request(flow)
        }
        if (writeFileResponse.commandStatus != Flipper.CommandStatus.OK) {
            throw FailedUploadSubGhzException()
        }
    }
}
