package com.flipperdevices.share.api

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent

interface CryptoStorageApi {
    suspend fun upload(flipperKey: FlipperKey): Result<String>
    suspend fun download(id: String, key: String): Result<FlipperKeyContent>
}
