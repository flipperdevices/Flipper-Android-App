package com.flipperdevices.bridge.rpc.api

interface FlipperStorageApi {
    suspend fun mkdirs(path: String)
}
