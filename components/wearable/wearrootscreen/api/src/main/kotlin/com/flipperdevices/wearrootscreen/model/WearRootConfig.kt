package com.flipperdevices.wearrootscreen.model

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.serialization.Serializable

@Serializable
sealed class WearRootConfig {
    @Serializable
    data object KeysList : WearRootConfig()

    @Serializable
    data class OpenKey(val path: FlipperKeyPath) : WearRootConfig()
}
