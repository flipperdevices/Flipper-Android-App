package com.flipperdevices.bridge.conenction.feature.storageinfo.model
sealed class StorageStats {
    data object Error : StorageStats()

    data class Loaded(val total: Long, val free: Long) : StorageStats()
}
