package com.flipperdevices.infrared.impl.model

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.serialization.Serializable

@Serializable
sealed class InfraredNavigationConfig {
    @Serializable
    data class View(val keyPath: FlipperKeyPath) : InfraredNavigationConfig()

    @Serializable
    data class Edit(val keyPath: FlipperKeyPath) : InfraredNavigationConfig()

    @Serializable
    data class Rename(val keyPath: FlipperKeyPath) : InfraredNavigationConfig()

    @Serializable
    data class RemoteControl(val keyPath: FlipperKeyPath) : InfraredNavigationConfig()
}
