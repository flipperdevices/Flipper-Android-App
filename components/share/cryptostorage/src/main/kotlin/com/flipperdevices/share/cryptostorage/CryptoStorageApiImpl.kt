package com.flipperdevices.share.cryptostorage

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.share.api.CryptoStorageApi
import com.squareup.anvil.annotations.ContributesBinding
import java.io.InputStream
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class CryptoStorageApiImpl @Inject constructor(
    private val cryptoHelperApi: CryptoHelperApi
) : CryptoStorageApi {
    override suspend fun upload(data: InputStream, path: String): Result<String> {
        return runCatching {
            val encrypt = cryptoHelperApi.encrypt(data.readBytes())
            val key = encrypt.first
            val decrypt = cryptoHelperApi.decode(encrypt.second, key)
            print(decrypt)
            ""
        }
    }
}
