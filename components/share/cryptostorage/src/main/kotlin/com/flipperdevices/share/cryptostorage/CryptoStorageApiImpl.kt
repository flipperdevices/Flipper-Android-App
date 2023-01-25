package com.flipperdevices.share.cryptostorage

import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
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
    override suspend fun upload(flipperKey: FlipperKey): Result<String> {
        return runCatching {
            val flipperFile = flipperKey.getShadowFile() ?: flipperKey.mainFile
            val encryptedData = cryptoHelperApi.encrypt(flipperFile)

            val storageLink = storageHelperApi.upload(data = encryptedData.data)
            val flipperKeyCrypto = FlipperKeyCrypto(
                fileId = getFileId(storageLink),
                cryptoKey = encryptedData.key,
                pathToKey = flipperKey.path.pathToKey
            )

            return@runCatching keyParser.cryptoKeyDataToUri(key = flipperKeyCrypto)
        }
    }

    override suspend fun download(id: String, key: String): Result<FlipperKeyContent> {
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
