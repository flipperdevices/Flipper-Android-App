package com.flipperdevices.remotecontrols.impl.api.model

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Serializable
@Stable
sealed interface RemoteControlsNavigationConfig {
    @Serializable
    data object SelectCategory : RemoteControlsNavigationConfig

    @Serializable
    data class Brands(val categoryId: Long) : RemoteControlsNavigationConfig

    @Serializable
    class Setup(val categoryId: Long, val brandId: Long) : RemoteControlsNavigationConfig

    @Serializable
    class Grid(val ifrFileId: Long) : RemoteControlsNavigationConfig
}
