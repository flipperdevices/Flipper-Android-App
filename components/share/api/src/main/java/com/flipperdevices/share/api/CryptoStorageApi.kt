package com.flipperdevices.share.api

interface CryptoStorageApi {
    suspend fun upload(data: ByteArray, path: String, name: String): Result<String>
    suspend fun download(id: String, key: String, name: String): Result<ByteArray>
}
