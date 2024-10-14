package com.flipperdevices.updater.downloader.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.updater.api.DownloadAndUnpackDelegateApi
import com.flipperdevices.updater.model.DistributionFile
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.content.ProgressListener
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.coroutines.withContext
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.File
import javax.inject.Inject

private const val TRY_MAX_COUNT = 3

@ContributesBinding(AppGraph::class, DownloadAndUnpackDelegateApi::class)
class DownloadAndUnpackDelegateApiImpl @Inject constructor(
    private val client: HttpClient
) : DownloadAndUnpackDelegateApi, LogTagProvider {
    override val TAG = "DownloadAndUnpackDelegate"

    override suspend fun download(
        distributionFile: DistributionFile,
        target: File,
        onProgress: (suspend (Long, Long?) -> Unit)?
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        var tryCount = 0
        var isSuccess = false
        while (!isSuccess) {
            try {
                downloadInternal(distributionFile, target, onProgress)
                isSuccess = true
            } catch (throwable: Throwable) {
                tryCount++
                error(throwable) { "While download $distributionFile to ${target.absolutePath}" }
                if (tryCount > TRY_MAX_COUNT) {
                    throw throwable
                }
            }
        }
    }

    private suspend fun downloadInternal(
        distributionFile: DistributionFile,
        target: File,
        onProgress: ProgressListener? = null
    ) {
        val channel = client.get(distributionFile.url) {
            onDownload(onProgress)
        }.bodyAsChannel()
        if (target.exists()) {
            target.delete()
        }

        val writeChannel = target.writeChannel()
        channel.copyAndClose(writeChannel)

        val hash = target.inputStream().use {
            String(Hex.encodeHex(DigestUtils.sha256(it)))
        }

        check(hash == distributionFile.sha256 || distributionFile.sha256 == null) {
            "Hash mismatch. Expected: ${distributionFile.sha256}, actual: $hash"
        }
    }

    @Suppress("NestedBlockDepth")
    override suspend fun unpack(source: File, target: File) {
        target.deleteRecursively()
        target.mkdirs()
        source.inputStream().use {
            val tarInputStream = TarArchiveInputStream(GzipCompressorInputStream(it))

            var entry: TarArchiveEntry? = tarInputStream.nextEntry ?: return
            do {
                if (entry != null) {
                    val entryFile = File(target, entry.name)

                    if (entry.isDirectory) {
                        entryFile.mkdirs()
                    } else {
                        tarInputStream.copyTo(entryFile.outputStream())
                    }
                }
                entry = tarInputStream.nextEntry
            } while (entry != null)
        }
    }
}
