package com.flipperdevices.infrared.editor.model

import androidx.annotation.StringRes
import com.flipperdevices.infrared.editor.core.model.InfraredRemote
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface InfraredEditorState {
    data object InProgress : InfraredEditorState
    data class Error(@StringRes val reason: Int) : InfraredEditorState
    data class Ready(
        val remotes: ImmutableList<InfraredRemote>,
        val keyName: String,
        val activeRemote: Int? = null,
        val errorRemotes: ImmutableList<Int> = persistentListOf(),
    ) : InfraredEditorState
}
