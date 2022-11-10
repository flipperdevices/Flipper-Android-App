package com.flipperdevices.share.api

import java.io.InputStream

interface CryptoStorageApi {
    suspend fun upload(data: InputStream, path: String): Result<String>
}
