package com.flipperdevices.bridge.api.model

data class FlipperRpcInformation(
    val internalStorageTotal: Long? = null,
    val internalStorageFree: Long? = null,
    val externalStorageTotal: Long? = null,
    val externalStorageFree: Long? = null,
    val otherFields: Map<String, String> = emptyMap()
)
