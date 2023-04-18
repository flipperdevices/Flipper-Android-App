package com.flipperdevices.updater.downloader.api

import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.preference.FlipperStorageProvider
import com.flipperdevices.updater.api.DownloadAndUnpackDelegateApi
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.downloader.model.ArtifactType
import com.flipperdevices.updater.downloader.model.FirmwareDirectoryListeningResponse
import com.flipperdevices.updater.downloader.model.SubGhzProvisioningResponse
import com.flipperdevices.updater.downloader.model.Target
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.DownloadProgress
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.SubGhzProvisioningException
import com.flipperdevices.updater.model.SubGhzProvisioningModel
import com.flipperdevices.updater.model.VersionFiles
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.io.File
import java.util.EnumMap
import javax.inject.Inject

private const val UPDATER_URL = "https://up.unleashedflip.com/directory.json"
private const val SUB_GHZ_URL = "https://update.flipperzero.one/regions/api/v0/bundle"

@ContributesBinding(AppGraph::class, DownloaderApi::class)
class DownloaderApiImpl @Inject constructor(
    private val context: Context,
    private val client: HttpClient,
    private val downloadAndUnpackDelegateApi: DownloadAndUnpackDelegateApi
) : DownloaderApi, LogTagProvider {
    override val TAG = "DownloaderApi"

    override suspend fun getLatestVersion(): EnumMap<FirmwareChannel, VersionFiles> {
        val versionMap: EnumMap<FirmwareChannel, VersionFiles> =
            EnumMap(FirmwareChannel::class.java)

        val response = client.get(
            urlString = UPDATER_URL
        ).body<FirmwareDirectoryListeningResponse>()

        verbose { "Receive response from server" }

        response.channels.map { channel ->
            channel.id to channel.versions.maxByOrNull { it.timestamp }
        }.filter { it.first != null && it.second != null }
            .map { it.first!! to it.second!! }
            .forEach { (channel, version) ->
                val updaterFile = version
                    .files
                    .filter { it.type == ArtifactType.UPDATE_TGZ }
                    .find { it.target == Target.F7 }
                    ?: return@forEach

                versionMap[channel.original] = VersionFiles(
                    version = FirmwareVersion(
                        channel.original,
                        version.version.clearVersion()
                    ),
                    updaterFile = DistributionFile(
                        updaterFile.url,
                        updaterFile.sha256
                    ),
                    changelog = version.changelog
                )
            }

        verbose { "Result version map is $versionMap" }

        return versionMap
    }

    @Throws(SubGhzProvisioningException::class)
    override suspend fun getSubGhzProvisioning(): SubGhzProvisioningModel {
        val response = client.get(
            urlString = SUB_GHZ_URL
        ).body<SubGhzProvisioningResponse>()

        verbose { "Receive subghz info $response" }

        if (response.error != null) {
            throw SubGhzProvisioningException(response.error.code, response.error.text)
        }
        val successfulResponse =
            response.success ?: throw SubGhzProvisioningException(
                errorCode = -1,
                "Not found response"
            )

        return SubGhzProvisioningModel(
            countries = successfulResponse
                .countriesBands
                .mapKeys { it.key.uppercase() }
                .mapValues { entry ->
                    entry.value.mapNotNull {
                        successfulResponse.bands[it]?.toSubGhzProvisioningBand()
                    }
                },
            country = successfulResponse.countryCode?.uppercase(),
            defaults = successfulResponse
                .defaultBands
                .mapNotNull { successfulResponse.bands[it]?.toSubGhzProvisioningBand() }
        )
    }

    override fun download(
        distributionFile: DistributionFile,
        target: File,
        decompress: Boolean
    ): Flow<DownloadProgress> = channelFlow {
        info { "Request download $distributionFile" }
        if (decompress) {
            FlipperStorageProvider.useTemporaryFile(context) { tempFile ->
                downloadAndUnpackDelegateApi.download(
                    distributionFile,
                    tempFile
                ) { processedBytes, totalBytes ->
                    require(totalBytes > 0) { "Server send total bytes less 0" }
                    send(DownloadProgress.InProgress(processedBytes, totalBytes))
                }
                info { "File downloaded in ${tempFile.absolutePath}" }

                downloadAndUnpackDelegateApi.unpack(tempFile, target)
                info {
                    "Unpack finished in ${target.absolutePath} ${target.listFiles()?.size} files"
                }
            }
        } else {
            downloadAndUnpackDelegateApi.download(
                distributionFile,
                target
            ) { processedBytes, totalBytes ->
                send(DownloadProgress.InProgress(processedBytes, totalBytes))
            }
            info { "File downloaded in ${target.absolutePath}" }
        }
    }
}

private fun String.clearVersion(): String {
    return replace("-rc", "").trim()
}
