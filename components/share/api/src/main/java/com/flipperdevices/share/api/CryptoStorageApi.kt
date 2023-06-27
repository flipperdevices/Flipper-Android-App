package com.flipperdevices.share.api

import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent

interface CryptoStorageApi {
    suspend fun upload(keyContent: FlipperKeyContent, path: String, name: String): Result<String>
    suspend fun download(
        id: String,
        key: String,
        name: String,
    ): Result<FlipperKeyContent>
}
