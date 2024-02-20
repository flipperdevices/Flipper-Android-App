package com.flipperdevices.keyscreen.impl.model

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.serialization.Serializable

@Serializable
sealed class KeyScreenNavigationConfig {
    @Serializable
    data class Main(val keyPath: FlipperKeyPath) : KeyScreenNavigationConfig()

    @Serializable
    data class NfcEdit(val keyPath: FlipperKeyPath) : KeyScreenNavigationConfig()

    @Serializable
    data class KeyEdit(val keyPath: FlipperKeyPath) : KeyScreenNavigationConfig()
}
