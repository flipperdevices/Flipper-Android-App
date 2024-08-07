package com.flipperdevices.remotecontrols.impl.grid.remote.presentation.model

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import kotlinx.serialization.Serializable

@Serializable
sealed class GridNavigationConfig {
    @Serializable
    data class Rename(val notSavedFlipperKey: NotSavedFlipperKey) : GridNavigationConfig()

    @Serializable
    data class SavedControl(val keyPath: FlipperKeyPath) : GridNavigationConfig()

    @Serializable
    data class ServerControl(val id: Long) : GridNavigationConfig()
}
