package com.flipperdevices.updater.downloader.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.updater.model.DistributionFile
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.features.onDownload
import io.ktor.client.request.get
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.copyAndClose
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

private const val TRY_MAX_COUNT = 3

interface DownloadAndUnpackDelegate {
    suspend fun download(
        distributionFile: DistributionFile,
        target: File,
        onProgress: (suspend (Long, Long) -> Unit)? = null
    )

    suspend fun unpack(source: File, target: File)
}

@ContributesBinding(AppGraph::class, DownloadAndUnpackDelegate::class)
class DownloadAndUnpackDelegateImpl @Inject constructor(
    private val client: HttpClient
) : DownloadAndUnpackDelegate, LogTagProvider {
    override val TAG = "DownloadAndUnpackDelegate"

    override suspend fun download(
        distributionFile: DistributionFile,
        target: File,
        onProgress: (suspend (Long, Long) -> Unit)?
    ) = withContext(Dispatchers.IO) {
        var tryCount = 0
        var isSuccess = false
        while (!isSuccess) {
            try {
                downloadInternal(distributionFile, target, onProgress)
                isSuccess = true
            } catch (@Suppress("TooGenericExceptionCaught") throwable: Throwable) {
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
        onProgress: (suspend (Long, Long) -> Unit)? = null
    ) {
        val channel = client.get<ByteReadChannel>(distributionFile.url) {
            onDownload(onProgress)
        }
        if (target.exists()) {
            target.delete()
        }

        val writeChannel = target.writeChannel()
        channel.copyAndClose(writeChannel)

        val hash = target.inputStream().use {
            String(Hex.encodeHex(DigestUtils.sha256(it)))
        }

        if (hash != distributionFile.sha256) {
            throw IllegalStateException(
                "Hash mismatch. Expected: ${distributionFile.sha256}, actual: $hash"
            )
        }
    }

    @Suppress("NestedBlockDepth")
    override suspend fun unpack(source: File, target: File) {
        target.deleteRecursively()
        target.mkdirs()
        source.inputStream().use {
            val tarInputStream = TarArchiveInputStream(GzipCompressorInputStream(it))

            var entry: TarArchiveEntry? = tarInputStream.nextTarEntry ?: return
            do {
                if (entry != null) {
                    val entryFile = File(target, entry.name)

                    if (entry.isDirectory) {
                        entryFile.mkdirs()
                    } else {
                        tarInputStream.copyTo(entryFile.outputStream())
                    }
                }
                entry = tarInputStream.nextTarEntry
            } while (entry != null)
        }
    }
}
