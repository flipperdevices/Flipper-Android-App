package com.flipperdevices.updater.downloader.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.downloader.di.DownloaderComponent
import com.flipperdevices.updater.downloader.model.ArtifactType
import com.flipperdevices.updater.downloader.model.FirmwareDirectoryListeningResponse
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.VersionFiles
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import java.util.EnumMap
import javax.inject.Inject

private const val JSON_URL = "https://update.flipperzero.one/firmware/directory.json"

@ContributesBinding(AppGraph::class, DownloaderApi::class)
class DownloaderApiImpl @Inject constructor() : DownloaderApi, LogTagProvider {
    override val TAG = "DownloaderApi"

    @Inject
    lateinit var client: HttpClient

    init {
        ComponentHolder.component<DownloaderComponent>().inject(this)
    }

    override suspend fun getLatestVersion(): EnumMap<FirmwareChannel, VersionFiles> {
        val versionMap: EnumMap<FirmwareChannel, VersionFiles> =
            EnumMap(FirmwareChannel::class.java)

        val response = try {
            client.get<FirmwareDirectoryListeningResponse>(JSON_URL)
        } catch (@Suppress("TooGenericExceptionCaught") exception: Throwable) {
            error(exception) { "When try receive latest version number" }
            return versionMap
        }

        response.channels.map { channel ->
            channel.id to channel.versions.maxByOrNull { it.timestamp }!!
        }.filterNot { it.first != null }
            .map { it.first!! to it.second }
            .forEach { (channel, version) ->
                val updaterFile = version.files.find { it.type == ArtifactType.UPDATE_TGZ }
                    ?: return@forEach

                versionMap[channel.original] = VersionFiles(
                    FirmwareVersion(
                        channel.original,
                        version.version.clearVersion()
                    ),
                    updaterFile = DistributionFile(
                        updaterFile.url,
                        updaterFile.sha256
                    )
                )
            }

        return versionMap
    }
}

private fun String.clearVersion(): String {
    return replace("-rc", "").trim()
}
