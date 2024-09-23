package com.flipperdevices.newfilemanager.impl.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface FileManagerState {
    val currentPath: String

    data class Ready(
        override val currentPath: String,
        val filesInDirectory: ImmutableList<FileItem> = persistentListOf(),
        val inProgress: Boolean
    ) : FileManagerState

    data class Error(
        override val currentPath: String
    ) : FileManagerState
}
