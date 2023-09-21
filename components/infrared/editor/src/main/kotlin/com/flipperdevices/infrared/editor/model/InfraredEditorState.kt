package com.flipperdevices.infrared.editor.model

import androidx.annotation.StringRes
import kotlinx.collections.immutable.ImmutableList

sealed interface InfraredEditorState {
    data object InProgress : InfraredEditorState
    data class Error(@StringRes val reason: Int) : InfraredEditorState
    data class Ready(
        val remotes: ImmutableList<InfraredRemote>,
        val keyName: String
    ) : InfraredEditorState
}
