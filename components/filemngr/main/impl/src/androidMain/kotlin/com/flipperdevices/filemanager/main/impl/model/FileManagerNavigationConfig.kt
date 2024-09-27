package com.flipperdevices.filemanager.main.impl.model

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable
import okio.Path
import okio.Path.Companion.toPath

@Stable
@Serializable
sealed interface FileManagerNavigationConfig {
    data class FileTree(val path: Path) : FileManagerNavigationConfig
    companion object {
        val DefaultFileTree: FileTree
            get() = FileTree("/".toPath())
    }
}
