package com.flipperdevices.remotecontrols.impl.api.model

import androidx.compose.runtime.Stable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
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
    sealed interface Grid : RemoteControlsNavigationConfig {
        data class Id(val ifrFileId: Long) : Grid
        data class Path(val flipperKeyPath: FlipperKeyPath) : Grid
    }
}
