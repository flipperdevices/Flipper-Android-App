package com.flipperdevices.share.cryptostorage

import com.flipperdevices.bridge.dao.api.PATH_FOR_FFF_SECURE_LIHK
import com.flipperdevices.bridge.dao.api.PREFFERED_HOST
import com.flipperdevices.bridge.dao.api.PREFFERED_SCHEME
import com.flipperdevices.bridge.dao.api.QUERY_DELIMITED_CHAR
import com.flipperdevices.bridge.dao.api.QUERY_KEY
import com.flipperdevices.bridge.dao.api.QUERY_KEY_PATH
import com.flipperdevices.bridge.dao.api.QUERY_VALUE_DELIMITED_CHAR
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.share.api.CryptoStorageApi
import com.flipperdevices.share.cryptostorage.helper.CryptoHelperApi
import com.flipperdevices.share.cryptostorage.helper.StorageHelperApi
import com.squareup.anvil.annotations.ContributesBinding
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class CryptoStorageApiImpl @Inject constructor(
    private val cryptoHelperApi: CryptoHelperApi,
    private val storageHelperApi: StorageHelperApi
) : CryptoStorageApi {
    override suspend fun upload(data: ByteArray, path: String, name: String): Result<String> {
        return runCatching {
            val encryptedData = cryptoHelperApi.encrypt(data)

            val storageLink = storageHelperApi.upload(
                data = encryptedData.data,
                name = name
            )

            val url = generateUrl(
                path = path,
                key = encryptedData.key,
                fileId = getFileId(storageLink)
            )

            return@runCatching url.toString()
        }
    }

    override suspend fun download(id: String, key: String, name: String): Result<ByteArray> {
        return runCatching {
            val downloadedData = storageHelperApi.download(
                id = id,
                name = name
            )
            return@runCatching cryptoHelperApi.decrypt(downloadedData, key)
        }
    }

    private fun generateUrl(
        path: String,
        key: String,
        fileId: String
    ): URL {
        val query = listOf(QUERY_KEY_PATH to path, QUERY_KEY to key)
            .filterNot { it.first.isBlank() || it.second.isBlank() }
            .joinToString(QUERY_DELIMITED_CHAR) {
                val field = URLEncoder.encode(it.first.trim(), "UTF-8")
                val value = URLEncoder.encode(it.second.trim(), "UTF-8")
                    .replace("%2F", "/") // We want safe / for readability
                "$field$QUERY_VALUE_DELIMITED_CHAR$value"
            }
        return URL(
            PREFFERED_SCHEME,
            PREFFERED_HOST,
            "$PATH_FOR_FFF_SECURE_LIHK/$fileId#$query"
        )
    }

    // https://transfer.flpr.app/c8llvE/hello.txt -> c8llvE
    private fun getFileId(link: String): String {
        val split = link.split("/")
        return split[split.size - 2]
    }
}
