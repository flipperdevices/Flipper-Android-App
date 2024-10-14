package com.flipperdevices.share.cryptostorage

import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyCrypto
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyparser.api.KeyParser
import com.flipperdevices.share.api.CryptoStorageApi
import com.flipperdevices.share.cryptostorage.helper.DecryptHelper
import com.flipperdevices.share.cryptostorage.helper.EncryptHelper
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import java.io.FileNotFoundException
import java.net.UnknownServiceException
import javax.inject.Inject

internal const val ALGORITHM_HELPER = "AES/GCM/NoPadding"
internal const val KEY_SIZE = 128
internal const val TAG_LENGTH = 16
internal const val BIT_SIZE = 8
internal const val IV_LENGTH = 12

private const val STORAGE_URL = "https://transfer.flpr.app/"
private const val STORAGE_NAME = "hakuna-matata"

@ContributesBinding(AppGraph::class)
class CryptoStorageApiImpl @Inject constructor(
    private val keyParser: KeyParser,
    private val client: HttpClient,
    private val storageProvider: FlipperStorageProvider
) : CryptoStorageApi {
    override suspend fun upload(
        keyContent: FlipperKeyContent,
        path: String,
        name: String
    ): Result<String> {
        return runCatching {
            val encryptHelper = EncryptHelper(keyContent)

            val response = client.put(
                urlString = "$STORAGE_URL$STORAGE_NAME"
            ) {
                setBody(
                    object : OutgoingContent.WriteChannelContent() {
                        override suspend fun writeTo(channel: ByteWriteChannel) {
                            encryptHelper.writeEncrypt(channel)
                        }
                    }
                )
            }

            when (response.status) {
                HttpStatusCode.OK -> {}
                else -> throw UnknownServiceException()
            }
            val storageLink: String = response.body()

            return@runCatching keyParser.cryptoKeyDataToUri(
                key = FlipperKeyCrypto(
                    fileId = getFileId(storageLink),
                    cryptoKey = encryptHelper.getKeyString(),
                    pathToKey = path
                )
            )
        }
    }

    override suspend fun download(
        id: String,
        key: String,
        name: String,
    ): Result<FlipperKeyContent> {
        return runCatching {
            val tempFile = storageProvider.getTemporaryFile().toFile()
            val decryptHelper = DecryptHelper()

            val response = client.get(urlString = "${STORAGE_URL}$id/$STORAGE_NAME")

            when (response.status) {
                HttpStatusCode.OK -> {}
                HttpStatusCode.NotFound -> throw FileNotFoundException()
                else -> throw UnknownServiceException()
            }
            response.bodyAsChannel().use { inputStream ->
                decryptHelper.writeDecrypt(inputStream, tempFile, key)
            }

            return@runCatching FlipperKeyContent.InternalFile(tempFile.absolutePath)
        }
    }

    // https://transfer.flpr.app/c8llvE/hello.txt -> c8llvE
    private fun getFileId(link: String): String {
        val split = link.split("/")
        return split[split.size - 2]
    }
}

suspend fun <R> ByteReadChannel.use(block: suspend (ByteReadChannel) -> R): R {
    var exception: Throwable? = null
    try {
        return block(this)
    } catch (throwable: Throwable) {
        exception = throwable
        throw throwable
    } finally {
        cancel(exception)
    }
}
