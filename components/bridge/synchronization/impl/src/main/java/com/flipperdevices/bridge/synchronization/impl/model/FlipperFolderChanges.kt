package com.flipperdevices.bridge.synchronization.impl.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlipperFolderChanges(
    @SerialName("current")
    private val currentTimeMs: Long = System.currentTimeMillis(),
    @SerialName("changes")
    private val lastChangesTimestamp: Map<String, Long> = emptyMap()
)