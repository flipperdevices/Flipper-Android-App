package com.flipperdevices.newfilemanager.impl.model

sealed class EditorState {
    data class Loading(
        val progress: DownloadProgress
    ) : EditorState()

    data class Loaded(
        val path: String,
        val content: String,
        val tooLarge: Boolean
    ) : EditorState()

    data class Saving(
        val progress: DownloadProgress
    ) : EditorState()

    data object Saved : EditorState()

    data object Error : EditorState()
}
