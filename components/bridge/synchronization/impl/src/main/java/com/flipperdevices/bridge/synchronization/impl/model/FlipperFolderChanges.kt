package com.flipperdevices.bridge.synchronization.impl.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlipperFolderChanges(
    @SerialName("changes_types")
    val lastChangesTimestampMap: Map<String, Long> = emptyMap()
)