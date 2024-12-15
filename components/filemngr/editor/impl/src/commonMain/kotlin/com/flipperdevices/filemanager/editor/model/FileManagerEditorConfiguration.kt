package com.flipperdevices.filemanager.editor.model

import com.flipperdevices.filemanager.util.serialization.PathSerializer
import kotlinx.serialization.Serializable
import okio.Path

@Serializable
sealed interface FileManagerEditorConfiguration {
    @Serializable
    data class Download(
        @Serializable(PathSerializer::class)
        val fullPathOnFlipper: Path
    ) : FileManagerEditorConfiguration

    @Serializable
    data class Editor(
        @Serializable(PathSerializer::class)
        val fullPathOnFlipper: Path,
        @Serializable(PathSerializer::class)
        val tempPathOnDevice: Path
    ) : FileManagerEditorConfiguration

    @Serializable
    data class Upload(
        @Serializable(PathSerializer::class)
        val fullPathOnFlipper: Path,
        @Serializable(PathSerializer::class)
        val tempPathOnDevice: Path
    ) : FileManagerEditorConfiguration
}
