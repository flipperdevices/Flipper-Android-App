package com.flipperdevices.newfilemanager.impl.model

sealed interface ShareState {
    data class Ready(
        val name: String,
        val downloadProgress: DownloadProgress = DownloadProgress.Infinite(),
        val processCompleted: Boolean = false
    ) : ShareState

    data object Error : ShareState
}
