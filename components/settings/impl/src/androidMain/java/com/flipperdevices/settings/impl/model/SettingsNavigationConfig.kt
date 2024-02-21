package com.flipperdevices.settings.impl.model

import kotlinx.serialization.Serializable

@Serializable
sealed class SettingsNavigationConfig {
    @Serializable
    data object Main : SettingsNavigationConfig()

    @Serializable
    data object FileManager : SettingsNavigationConfig()

    @Serializable
    data object Shake2Report : SettingsNavigationConfig()

    @Serializable
    data object StressTest : SettingsNavigationConfig()
}
