package com.flipperdevices.filemanager.impl.model

import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

data class FileManagerState(
    val currentPath: String,
    val filesInDirectory: ImmutableSet<FileItem> = persistentSetOf(),
    val inProgress: Boolean = true
)
