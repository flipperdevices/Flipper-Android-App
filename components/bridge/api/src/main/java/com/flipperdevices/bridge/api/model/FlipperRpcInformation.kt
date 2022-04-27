package com.flipperdevices.bridge.api.model

data class FlipperRpcInformation(
    val internalStorageStats: StorageStats? = null,
    val externalStorageStats: StorageStats? = null,
    val otherFields: Map<String, String> = emptyMap()
)

sealed class StorageStats {
    object Error : StorageStats()

    data class Loaded(val total: Long, val free: Long) : StorageStats()
}
