package com.flipperdevices.bridge.synchronization.impl.model

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManifestFile(
    @SerialName("keys")
    val keys: List<KeyWithHash>,
    @SerialName("favorites")
    val favorites: List<FlipperFilePath> = emptyList(),
    @SerialName("favorites_from_flipper")
    val favoritesFromFlipper: List<FlipperFilePath> = emptyList(),
    @SerialName("folder_changes")
    val folderChanges: FlipperFolderChanges = FlipperFolderChanges()
)
