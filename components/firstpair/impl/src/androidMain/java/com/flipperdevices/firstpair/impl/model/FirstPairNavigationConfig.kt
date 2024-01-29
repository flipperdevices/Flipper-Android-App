package com.flipperdevices.firstpair.impl.model

import kotlinx.serialization.Serializable

@Serializable
sealed class FirstPairNavigationConfig {
    @Serializable
    data object TOSScreen : FirstPairNavigationConfig()

    @Serializable
    data object DeviceScreen : FirstPairNavigationConfig()

    @Serializable
    data object HelpScreen : FirstPairNavigationConfig()
}
