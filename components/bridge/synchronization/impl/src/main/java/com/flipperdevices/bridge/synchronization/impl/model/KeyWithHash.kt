package com.flipperdevices.bridge.synchronization.impl.model

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class KeyWithHash constructor(
    @JsonNames("keyPath")
    @SerialName("key_path")
    val keyPath: FlipperKeyPath,
    @SerialName("hash")
    val hash: String
)
