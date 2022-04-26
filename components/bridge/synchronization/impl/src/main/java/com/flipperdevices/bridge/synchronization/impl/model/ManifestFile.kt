package com.flipperdevices.bridge.synchronization.impl.model

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManifestFile(
    @SerialName("keys")
    val keys: List<KeyWithHash>,
    @SerialName("favorites")
    val favorites: List<FlipperKeyPath> = emptyList()
)
