package com.flipperdevices.bridge.rpc.api

interface FlipperStorageApi {
    suspend fun mkdirs(path: String)

    suspend fun delete(path: String, recursive: Boolean = false)
}
