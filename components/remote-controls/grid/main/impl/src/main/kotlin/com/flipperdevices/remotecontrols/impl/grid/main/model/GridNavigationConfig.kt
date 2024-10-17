package com.flipperdevices.remotecontrols.impl.grid.main.model

import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import kotlinx.serialization.Serializable

@Serializable
sealed class GridNavigationConfig {
    @Serializable
    data class Rename(val notSavedFlipperKey: NotSavedFlipperKey) : GridNavigationConfig()

    @Serializable
    data class ServerControl(val id: Long, val remoteName: String) : GridNavigationConfig()
}
