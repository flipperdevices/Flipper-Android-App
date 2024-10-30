package com.flipperdevices.filemanager.main.impl.model

import androidx.compose.runtime.Stable
import com.flipperdevices.filemanager.main.serialization.PathSerializer
import kotlinx.serialization.Serializable
import okio.Path
import okio.Path.Companion.toPath

@Stable
@Serializable
sealed interface FileManagerNavigationConfig {
    @Serializable
    data class FileTree(
        @Serializable(with = PathSerializer::class)
        val path: Path
    ) : FileManagerNavigationConfig

    @Serializable
    data class Upload(
        @Serializable(with = PathSerializer::class)
        val path: Path
    ) : FileManagerNavigationConfig

    @Serializable
    data class Edit(
        @Serializable(with = PathSerializer::class)
        val path: Path
    ) : FileManagerNavigationConfig

    @Serializable
    data class Search(
        @Serializable(with = PathSerializer::class)
        val path: Path
    ) : FileManagerNavigationConfig

    companion object {
        val DefaultFileTree: FileTree
            get() = FileTree("/".toPath())
    }
}
