package com.flipperdevices.remotecontrols.impl.api.model

import androidx.compose.runtime.Stable
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import kotlinx.serialization.Serializable

@Serializable
@Stable
sealed interface RemoteControlsNavigationConfig {
    @Serializable
    data object SelectCategory : RemoteControlsNavigationConfig

    @Serializable
    data class Brands(
        val categoryId: Long,
        val categoryName: String
    ) : RemoteControlsNavigationConfig

    @Serializable
    data class Infrareds(
        val brandId: Long,
    ) : RemoteControlsNavigationConfig

    @Serializable
    class Setup(
        val categoryId: Long,
        val brandId: Long,
        val categoryName: String,
        val brandName: String
    ) : RemoteControlsNavigationConfig

    @Serializable
    data class ServerRemoteControl(
        val infraredFileId: Long,
        val remoteName: String
    ) : RemoteControlsNavigationConfig

    @Serializable
    data class Rename(
        val notSavedFlipperKey: NotSavedFlipperKey
    ) : RemoteControlsNavigationConfig
}
