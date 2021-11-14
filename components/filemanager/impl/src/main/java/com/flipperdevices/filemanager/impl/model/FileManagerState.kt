package com.flipperdevices.filemanager.impl.model

data class FileManagerState(
    val currentPath: String,
    val filesInDirectory: Set<FileItem> = emptySet()
)
