package com.flipperdevices.filemanager.impl.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

sealed interface FileManagerState {
    data class Ready(
        val currentPath: String,
        val filesInDirectory: ImmutableList<FileItem> = persistentListOf(),
        val inProgress: Boolean
    ) : FileManagerState

    data object Error : FileManagerState

}
