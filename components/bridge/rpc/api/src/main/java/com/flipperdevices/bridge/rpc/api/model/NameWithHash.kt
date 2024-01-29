package com.flipperdevices.bridge.rpc.api.model

import com.flipperdevices.protobuf.storage.Storage

data class NameWithHash(
    val name: String,
    val md5: String,
    val size: Int,
    val type: Storage.File.FileType
)
