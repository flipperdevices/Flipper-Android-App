package com.flipperdevices.share.cryptostorage

import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperKeyCrypto
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.share.api.CryptoStorageApi
import com.flipperdevices.share.cryptostorage.helper.CryptoHelperApi
import com.flipperdevices.share.cryptostorage.helper.StorageHelperApi
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class CryptoStorageApiImpl @Inject constructor(
    private val cryptoHelperApi: CryptoHelperApi,
    private val storageHelperApi: StorageHelperApi,
    private val keyParser: KeyParser
) : CryptoStorageApi {
    override suspend fun upload(data: ByteArray, path: String, name: String): Result<String> {
        return runCatching {
            val encryptedData = cryptoHelperApi.encrypt(data)

            val storageLink = storageHelperApi.upload(data = encryptedData.data)

            return@runCatching keyParser.cryptoKeyDataToUri(
                key = FlipperKeyCrypto(
                    fileId = getFileId(storageLink),
                    cryptoKey = encryptedData.key,
                    pathToKey = path
                )
            )
        }
    }

    override suspend fun download(id: String, key: String, name: String): Result<ByteArray> {
        return runCatching {
            val downloadedData = storageHelperApi.download(id = id)
            return@runCatching cryptoHelperApi.decrypt(downloadedData, key)
        }
    }

    // https://transfer.flpr.app/c8llvE/hello.txt -> c8llvE
    private fun getFileId(link: String): String {
        val split = link.split("/")
        return split[split.size - 2]
    }
}
